package icbm.core;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import icbm.Reference;
import icbm.Settings;
import icbm.TabICBM;
import icbm.core.blocks.*;
import icbm.core.compat.Waila;
import icbm.core.entity.EntityFlyingBlock;
import icbm.core.entity.EntityFragments;
import icbm.core.implement.IChunkLoadHandler;
import icbm.core.items.ItemAntidote;
import icbm.core.items.ItemComputer;
import icbm.core.items.ItemPoisonPowder;
import icbm.core.items.ItemSignalDisrupter;
import icbm.core.items.ItemSulfurDust;
import icbm.core.items.ItemTracker;

import java.util.ArrayList;
import java.util.List;

import icbm.explosion.entities.*;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.explosive.TileExplosive;
import icbm.explosion.items.*;
import icbm.explosion.machines.TileMissileAssembler;
import icbm.explosion.potion.ContagiousPoison;
import icbm.explosion.potion.PoisonContagion;
import icbm.explosion.potion.PoisonFrostBite;
import icbm.explosion.potion.PoisonToxin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;
import resonant.api.explosion.ExplosionEvent;
import resonant.api.explosion.ExplosiveHelper;
import resonant.content.loader.ModManager;
import resonant.content.prefab.itemblock.ItemBlockMetadata;
import resonant.lib.config.Config;
import resonant.lib.config.ConfigHandler;
import resonant.lib.loadable.LoadableHandler;
import resonant.lib.recipe.RecipeUtility;
import resonant.lib.recipe.UniversalRecipe;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.PotionUtility;

/** Main class for ICBM core to run on. The core will need to be initialized by each ICBM module.
 * 
 * @author Calclavia */
@Mod(modid = Reference.NAME, name = Reference.NAME, version = Reference.VERSION, dependencies = "after:ResonantInduction|Atomic;required-after:ResonantEngine")
@ModstatInfo(prefix = "icbm", name = Reference.NAME, version = Reference.VERSION)
public final class ICBMCore
{
    @Instance(Reference.NAME)
    public static ICBMCore INSTANCE;

    @Metadata(Reference.NAME)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = "icbm.core.ClientProxy", serverSide = "icbm.core.CommonProxy")
    public static CommonProxy proxy;

    @Config(key = "Creepers_Drop_Sulfur", category = "Extras")
    public static boolean CREEPER_DROP_SULFER = true;
    @Config(key = "Creepers_Blow_up_in_Fire", category = "Extras")
    public static boolean CREEPER_BLOW_UP_IN_FIRE = true;
    @Config(key = "EntityIDStart", category = "Extras")
    public static int ENTITY_ID_PREFIX = 50;

    // Blocks
    public static Block blockGlassPlate;
    public static Block blockGlassButton;
    public static Block blockProximityDetector;
    public static Block blockSpikes;
    public static Block blockCamo;
    public static Block blockConcrete;
    public static Block blockReinforcedGlass;
    public static Block blockSulfurOre;
    public static Block blockRadioactive;
    public static Block blockCombatRail;
    public static Block blockExplosive;
    public static Block blockMachine;
    public static Block blockMissileAssembler;

    // Items
    public static Item itemAntidote;
    public static Item itemSignalDisrupter;
    public static Item itemTracker;
    public static Item itemHackingComputer;
    public static Item itemSulfurDust;
    public static Item itemPoisonPowder;
    public static Item itemMissile;
    public static Item itemDefuser;
    public static Item itemRadarGun;
    public static Item itemRemoteDetonator;
    public static Item itemLaserDesignator;
    public static Item itemRocketLauncher;
    public static Item itemGrenade;
    public static Item itemBombCart;

    //Potion effects
    public static final ContagiousPoison poisonous_potion = new ContagiousPoison("Chemical", 1, false);
    public static final ContagiousPoison contagios_potion = new ContagiousPoison("Contagious", 1, true);


    //Content loader, and manager
    private ModManager contentRegistry = new ModManager().setPrefix(Reference.PREFIX).setTab(TabICBM.INSTANCE);

    //Handler for loadable objects
    private LoadableHandler modproxies = new LoadableHandler();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        Modstats.instance().getReporter().registerMod(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(proxy);
        ExplosiveHelper.explosionManager = ExplosiveRegistry.class;

        // MODULES TO LOAD INTO MOD PHASE
        modproxies.applyModule(Waila.class, true);

        Settings.CONFIGURATION.load();

        //ResonantEngine.blockMulti.setTextureName(Reference.PREFIX + "machine");

        // Blocks       
        blockSulfurOre = contentRegistry.newBlock(BlockSulfurOre.class);
        blockGlassPlate = contentRegistry.newBlock("glassPressurePlate", new BlockPressurePlate("glassPressurePlate", Material.glass, BlockPressurePlate.Sensitivity.everything));
        blockGlassButton = contentRegistry.newBlock("glassButton", new BlockButton(false));
        blockProximityDetector = contentRegistry.newBlock(TileProximityDetector.class);
        blockSpikes = contentRegistry.newBlock(BlockSpikes.class, ItemBlockMetadata.class);
        blockCamo = contentRegistry.newBlock(TileCamouflage.class);
        blockConcrete = contentRegistry.newBlock(BlockConcrete.class, ItemBlockMetadata.class);
        blockReinforcedGlass = contentRegistry.newBlock(BlockReinforcedGlass.class, ItemBlockMetadata.class);
        blockCombatRail = contentRegistry.newBlock(BlockReinforcedRail.class);
        blockExplosive = contentRegistry.newBlock(TileExplosive.class);
        //TODO blockMachine = ICBMCore.contentRegistry.newBlock(BlockICBMMachine.class, ItemBlockMachine.class);
        blockMissileAssembler = contentRegistry.newBlock(TileMissileAssembler.class);

        // ITEMS
        itemPoisonPowder = contentRegistry.newItem(ItemPoisonPowder.class);
        itemSulfurDust = contentRegistry.newItem(ItemSulfurDust.class);
        itemAntidote = contentRegistry.newItem(ItemAntidote.class);
        itemSignalDisrupter = contentRegistry.newItem(ItemSignalDisrupter.class);
        itemTracker = contentRegistry.newItem(ItemTracker.class);
        itemHackingComputer = contentRegistry.newItem(ItemComputer.class);
        itemMissile = contentRegistry.newItem(ItemMissile.class);
        itemDefuser = contentRegistry.newItem(ItemDefuser.class);
        itemRadarGun = contentRegistry.newItem(ItemRadarGun.class);
        itemRemoteDetonator = contentRegistry.newItem(ItemRemoteDetonator.class);
        itemLaserDesignator = contentRegistry.newItem(ItemLaserDesignator.class);
        itemRocketLauncher = contentRegistry.newItem(ItemRocketLauncher.class);
        itemGrenade = contentRegistry.newItem(ItemGrenade.class);
        itemBombCart = contentRegistry.newItem(ItemBombCart.class);

        OreDictionary.registerOre("dustSulfur", new ItemStack(itemSulfurDust, 1, 0));
        OreDictionary.registerOre("dustSaltpeter", new ItemStack(itemSulfurDust, 1, 1));

        /** Check for existence of radioactive block. If it does not exist, then create it. */
        if (OreDictionary.getOres("blockRadioactive").size() > 0)
        {
            blockRadioactive = Block.getBlockFromItem(OreDictionary.getOres("blockRadioactive").get(0).getItem());
            Reference.LOGGER.fine("Detected radioative block from another mod, utilizing it.");
        }
        else
        {
            blockRadioactive = Blocks.mycelium;
        }

        PoisonToxin.INSTANCE = new PoisonToxin(PotionUtility.getNextOptimalPotId(), true, 5149489, "toxin");
        PoisonContagion.INSTANCE = new PoisonContagion(PotionUtility.getNextOptimalPotId(), false, 5149489, "virus");
        PoisonFrostBite.INSTANCE = new PoisonFrostBite(PotionUtility.getNextOptimalPotId(), false, 5149489, "frostBite");

        /** Decrease Obsidian Resistance */
        Blocks.obsidian.setResistance(Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45).getInt(45));
        Reference.LOGGER.fine("Changed obsidian explosive resistance to: " + Blocks.obsidian.getExplosionResistance(null));

        //Set creative tab
        TabICBM.itemStack = new ItemStack(blockProximityDetector);



        /** Dispenser Handler */
        BlockDispenser.dispenseBehaviorRegistry.putObject(itemGrenade, new DispenseBehaviorGrenade());
        BlockDispenser.dispenseBehaviorRegistry.putObject(itemBombCart, new DispenseBehaviorBombCart());

        /** Chunk loading handler. */
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ForgeChunkManager.LoadingCallback()
        {
            @Override
            public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
            {
                for (ForgeChunkManager.Ticket ticket : tickets)
                {
                    if (ticket.getEntity() instanceof IChunkLoadHandler)
                    {
                        ((IChunkLoadHandler) ticket.getEntity()).chunkLoaderInit(ticket);
                    } else
                    {
                        if (ticket.getModData() != null)
                        {
                            Vector3 position = new Vector3(ticket.getModData());

                            TileEntity tileEntity = position.getTileEntity(ticket.world);

                            if (tileEntity instanceof IChunkLoadHandler)
                            {
                                ((IChunkLoadHandler) tileEntity).chunkLoaderInit(ticket);
                            }
                        }
                    }
                }
            }
        });

        proxy.preInit();
        modproxies.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Settings.setModMetadata(Reference.NAME, Reference.NAME, metadata);

        EntityRegistry.registerGlobalEntityID(EntityFlyingBlock.class, "ICBMGravityBlock", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityFragments.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityExplosive.class, "ICBMExplosive", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityMissile.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityExplosion.class, "ICBMProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityLightBeam.class, "ICBMLightBeam", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityGrenade.class, "ICBMGrenade", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityBombCart.class, "ICBMBombCart", EntityRegistry.findGlobalUniqueEntityId());

        EntityRegistry.registerModEntity(EntityFlyingBlock.class, "ICBMGravityBlock", ENTITY_ID_PREFIX, this, 50, 15, true);
        EntityRegistry.registerModEntity(EntityFragments.class, "ICBMFragment", ENTITY_ID_PREFIX + 1, this, 40, 8, true);
        EntityRegistry.registerModEntity(EntityExplosive.class, "ICBMExplosive", ENTITY_ID_PREFIX + 2, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityMissile.class, "ICBMMissile", ENTITY_ID_PREFIX + 3, this, 500, 1, true);
        EntityRegistry.registerModEntity(EntityExplosion.class, "ICBMProceduralExplosion", ENTITY_ID_PREFIX + 4, this, 100, 5, true);
        EntityRegistry.registerModEntity(EntityLightBeam.class, "ICBMLightBeam", ENTITY_ID_PREFIX + 5, this, 80, 5, true);
        EntityRegistry.registerModEntity(EntityGrenade.class, "ICBMGrenade", ENTITY_ID_PREFIX + 6, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityBombCart.class, "ICBMBombCart", ENTITY_ID_PREFIX + 7, this, 50, 4, true);

        proxy.init();
        modproxies.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ConfigHandler.sync(Settings.CONFIGURATION, Settings.DOMAIN);

        /** LOAD. */
        ArrayList dustCharcoal = OreDictionary.getOres("dustCharcoal");
        ArrayList dustCoal = OreDictionary.getOres("dustCoal");
        // Sulfur
        GameRegistry.addSmelting(blockSulfurOre, new ItemStack(itemSulfurDust, 4), 0.8f);
        GameRegistry.addSmelting(Items.reeds, new ItemStack(itemSulfurDust, 4, 1), 0f);
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSulfur", "dustSaltpeter", Items.coal }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSulfur", "dustSaltpeter", new ItemStack(Items.coal, 1, 1) }));

        if (dustCharcoal != null && dustCharcoal.size() > 0)
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSulfur", "dustSaltpeter", "dustCharcoal" }));
        if (dustCoal != null && dustCoal.size() > 0)
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSulfur", "dustSaltpeter", "dustCoal" }));

        GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.tnt, new Object[] { "@@@", "@R@", "@@@", '@', Items.gunpowder, 'R', Items.redstone }));

        // Poison Powder
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemPoisonPowder, 3), new Object[] { Items.spider_eye, Items.rotten_flesh }));
        /** Add all Recipes */
        // Spikes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSpikes, 6), new Object[] { "CCC", "BBB", 'C', Blocks.cactus, 'B', Items.iron_ingot }));
        GameRegistry.addRecipe(new ItemStack(blockSpikes, 1, 1), new Object[] { "E", "S", 'E', itemPoisonPowder, 'S', blockSpikes });
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSpikes, 1, 2), new Object[] { "E", "S", 'E', itemSulfurDust, 'S', blockSpikes }));

        // Camouflage
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCamo, 12), new Object[] { "WGW", "G G", "WGW", 'G', Blocks.vine, 'W', Blocks.wool }));

        // Tracker
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTracker), new Object[] { " Z ", "SBS", "SCS", 'Z', Items.compass, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'B', UniversalRecipe.BATTERY.get(), 'S', Items.iron_ingot }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTracker), new Object[] { " Z ", "SBS", "SCS", 'Z', Items.compass, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'B', Items.ender_pearl, 'S', Items.iron_ingot }));

        // Glass Pressure Plate
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockGlassPlate, 1, 0), new Object[] { "##", '#', Blocks.glass }));

        // Glass Button
        GameRegistry.addRecipe(new ItemStack(blockGlassButton, 2), new Object[] { "G", "G", 'G', Blocks.glass });

        // Proximity Detector
        GameRegistry.addRecipe(new ShapedOreRecipe(blockProximityDetector, new Object[] { "SSS", "S?S", "SSS", 'S', Items.iron_ingot, '?', itemTracker }));

        // Signal Disrupter
        GameRegistry.addRecipe(new ShapedOreRecipe(itemSignalDisrupter, new Object[] { "WWW", "SCS", "SSS", 'S', Items.iron_ingot, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'W', UniversalRecipe.WIRE.get() }));

        // Antidote
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAntidote, 6), new Object[] { "@@@", "@@@", "@@@", '@', Items.pumpkin_seeds }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAntidote), new Object[] { "@@@", "@@@", "@@@", '@', Items.wheat_seeds}));

        // Concrete
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConcrete, 8, 0), new Object[] { "SGS", "GWG", "SGS", 'G', Blocks.gravel, 'S', Blocks.sand, 'W', Items.water_bucket }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConcrete, 8, 1), new Object[] { "COC", "OCO", "COC", 'C', new ItemStack(blockConcrete, 1, 0), 'O', Blocks.obsidian }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConcrete, 8, 2), new Object[] { "COC", "OCO", "COC", 'C', new ItemStack(blockConcrete, 1, 1), 'O', Items.iron_ingot }));

        // Reinforced rails
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCombatRail, 16, 0), new Object[] { "C C", "CIC", "C C", 'I', new ItemStack(blockConcrete, 1, 0), 'C', Items.iron_ingot }));

        // Reinforced Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockReinforcedGlass, 8), new Object[] { "IGI", "GIG", "IGI", 'G', Blocks.glass, 'I', Items.iron_ingot }));

        /** Add all Recipes */
        // Rocket Launcher
        //GameRegistry.addRecipe(new ShapedOreRecipe(itemRocketLauncher, new Object[] { "SCR", "SB ", 'R', itemRadarGun, 'C', new ItemStack(blockMachine, 1, MachineData.CruiseLauncher.ordinal() + 6), 'B', Blocks.stone_button, 'S', UniversalRecipe.PRIMARY_METAL.get() }));
        // Radar Gun
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRadarGun), new Object[] { "@#!", " $!", "  !", '@', Blocks.glass, '!', UniversalRecipe.PRIMARY_METAL.get(), '#', UniversalRecipe.CIRCUIT_T1.get(), '$', Blocks.stone_button }));
        // Remote
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRemoteDetonator), new Object[] { "?@@", "@#$", "@@@", '@', UniversalRecipe.PRIMARY_METAL.get(), '?', Items.redstone, '#', UniversalRecipe.CIRCUIT_T2.get(), '$', Blocks.stone_button }));
        // Laser Designator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemLaserDesignator), new Object[] { "!  ", " ? ", "  @", '@', itemRemoteDetonator, '?', UniversalRecipe.CIRCUIT_T3.get(), '!', itemRadarGun }));
        // Defuser
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemDefuser), new Object[] { "I  ", " W ", "  C", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'W', UniversalRecipe.WRENCH.get(), 'I', UniversalRecipe.WIRE.get() }));
        // Missile Launcher Platform
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 0), new Object[] { "! !", "!C!", "!!!", '!', UniversalRecipe.SECONDARY_METAL.get(), 'C', UniversalRecipe.CIRCUIT_T1.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 1), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(blockMachine, 1, 0), '!', UniversalRecipe.PRIMARY_METAL.get(), 'C', UniversalRecipe.CIRCUIT_T2.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 2), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(blockMachine, 1, 1), '!', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get() }));
        // Missile Launcher Panel
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 3), new Object[] { "!!!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T1.get(), '!', Blocks.glass, '?', UniversalRecipe.WIRE.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 4), new Object[] { "!$!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T2.get(), '!', UniversalRecipe.PRIMARY_METAL.get(), '?', UniversalRecipe.WIRE.get(), '$', new ItemStack(blockMachine, 1, 3) }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 5), new Object[] { "!$!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T3.get(), '!', Items.gold_ingot, '?', UniversalRecipe.WIRE.get(), '$', new ItemStack(blockMachine, 1, 4) }));
        // Missile Launcher Support Frame
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 6), new Object[] { "! !", "!!!", "! !", '!', UniversalRecipe.SECONDARY_METAL.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 7), new Object[] { "! !", "!@!", "! !", '!', UniversalRecipe.PRIMARY_METAL.get(), '@', new ItemStack(blockMachine, 1, 6) }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 8), new Object[] { "! !", "!@!", "! !", '!', UniversalRecipe.PRIMARY_PLATE.get(), '@', new ItemStack(blockMachine, 1, 7) }));
        // Radar Station
        //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 9), new Object[] { "?@?", " ! ", "!#!", '@', CompatibilityModule.getItemWithCharge(new ItemStack(itemRadarGun), 0), '!', UniversalRecipe.PRIMARY_PLATE.get(), '#', UniversalRecipe.CIRCUIT_T1.get(), '?', Items.gold_ingot }));
        // EMP Tower
        RecipeUtility.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 10), new Object[]{"?W?", "@!@", "?#?", '?', UniversalRecipe.PRIMARY_PLATE.get(), '!', UniversalRecipe.CIRCUIT_T3.get(), '@', UniversalRecipe.BATTERY_BOX.get(), '#', UniversalRecipe.MOTOR.get(), 'W', UniversalRecipe.WIRE.get()}), "EMP Tower", Settings.CONFIGURATION, true);
        // Cruise Launcher
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 11), new Object[] { "?! ", "@@@", '@', UniversalRecipe.PRIMARY_PLATE.get(), '!', new ItemStack(blockMachine, 1, 2), '?', new ItemStack(blockMachine, 1, 8) }));
        // Missile Coordinator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 12), new Object[] { "R R", "SCS", "SSS", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'R', itemRemoteDetonator }));
        // Missile module
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), new Object[] { " @ ", "@#@", "@?@", '@', UniversalRecipe.PRIMARY_METAL.get(), '?', Items.flint_and_steel, '#', UniversalRecipe.CIRCUIT_T1.get() }));
        // Homing
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.homing.getID()), new Object[] { " B ", " C ", "BMB", 'M', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), 'C', UniversalRecipe.CIRCUIT_T1.get(), 'B', UniversalRecipe.SECONDARY_METAL.get() }));
        // Anti-ballistic
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.antiBallistic.getID()), new Object[] { "!", "?", "@", '@', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), '?', new ItemStack(blockExplosive, 1, 0), '!', UniversalRecipe.CIRCUIT_T1.get() }));
        // Cluster
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.cluster.getID()), new Object[] { " ! ", " ? ", "!@!", '@', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), '?', Explosive.fragmentation.getItemStack(), '!', new ItemStack(itemMissile, 1, 0) }));
        // Nuclear Cluster
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.nuclearCluster.getID()), new Object[] { " N ", "NCN", 'C', new ItemStack(itemMissile, 1, Explosive.cluster.getID()), 'N', Explosive.nuclear.getItemStack() }));

        // Add all explosive recipes.
        //if (!Loader.isModLoaded("ResonantInduction|Atomic")) //TODO ? static id
        //OreDictionary.registerOre("strangeMatter", new ItemStack(397, 1, 1));

        for (Explosive explosive : ExplosiveRegistry.getExplosives())
        {
            explosive.init();
            // Missile
            RecipeUtility.addRecipe(new ShapelessOreRecipe(new ItemStack(itemMissile, 1, explosive.getID()), new Object[] { new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), new ItemStack(blockExplosive, 1, explosive.getID()) }), explosive.getUnlocalizedName() + " Missile", Settings.CONFIGURATION, true);
            if (explosive.getTier() < 2)
            {
                // Grenade
                RecipeUtility.addRecipe(new ShapedOreRecipe(new ItemStack(itemGrenade, 1, explosive.getID()), new Object[] { "?", "@", '@', new ItemStack(blockExplosive, 1, explosive.getID()), '?', Items.string }), explosive.getUnlocalizedName() + " Grenade", Settings.CONFIGURATION, true);
            }
            if (explosive.getTier() < 3)
            {
                // Minecart
                RecipeUtility.addRecipe(new ShapedOreRecipe(new ItemStack(itemBombCart, 1, explosive.getID()), new Object[] { "?", "@", '?', new ItemStack(blockExplosive, 1, explosive.getID()), '@', Items.minecart }), explosive.getUnlocalizedName() + " Minecart", Settings.CONFIGURATION, true);
            }
        }

        modproxies.postInit();
    }

    @SubscribeEvent
    public void creeperDropEvent(LivingDropsEvent evt)
    {
        if (evt.entityLiving instanceof EntityCreeper)
        {
            if (CREEPER_DROP_SULFER)
            {
                evt.entityLiving.dropItem(ICBMCore.itemSulfurDust, 1 + evt.entityLiving.worldObj.rand.nextInt(6));
            }
        }
    }

    @SubscribeEvent
    public void preDetonationEvent(ExplosionEvent.ExplosivePreDetonationEvent evt)
    {
        /** if (FlagRegistry.getModFlag() != null && evt.explosion instanceof Explosive)
         {
         if (((Explosive) evt.explosion).isBannedInRegion(evt.world, evt.x, evt.y, evt.z))
         {
         ICBMCore.LOGGER.fine("ICBM prevented explosive:" + evt.x + ", " + evt.y + "," + evt.z);
         evt.setCanceled(true);
         }
         } */
    }

    @SubscribeEvent
    public void preConstructionEvent(ExplosionEvent.ExplosionConstructionEvent evt)
    {
        /** if (FlagRegistry.getModFlag() != null && evt.iExplosion instanceof Explosive)
         {
         if (((Explosive) evt.iExplosion).isBannedInRegion(evt.world, evt.x, evt.y, evt.z))
         {
         ICBMCore.LOGGER.fine("ICBM prevented explosive:" + evt.x + ", " + evt.y + "," + evt.z);
         evt.setCanceled(true);
         }
         } */
    }

    @SubscribeEvent
    public void preExplosionEvent(ExplosionEvent.PreExplosionEvent evt)
    {
        /** if (FlagRegistry.getModFlag() != null && evt.iExplosion instanceof Explosive)
         {
         if (((Explosive) evt.iExplosion).isBannedInRegion(evt.world, evt.x, evt.y, evt.z))
         {
         ICBMCore.LOGGER.fine("ICBM prevented explosive:" + evt.x + ", " + evt.y + "," + evt.z);
         evt.setCanceled(true);
         }
         } */
    }

    @SubscribeEvent
    public void enteringChunk(EntityEvent.EnteringChunk evt)
    {
        if (evt.entity instanceof EntityMissile)
        {
            ((EntityMissile) evt.entity).updateLoadChunk(evt.newChunkX, evt.newChunkZ);
        }
    }

    @SubscribeEvent
    public void creeperDeathEvent(LivingDeathEvent evt)
    {
        if (evt.entityLiving instanceof EntityCreeper)
        {
            if (CREEPER_BLOW_UP_IN_FIRE)
            {
                if (evt.source == DamageSource.onFire || evt.source == DamageSource.inFire)
                {
                    evt.setCanceled(true);
                    boolean flag = evt.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

                    if (((EntityCreeper) evt.entityLiving).getPowered())
                    {
                        evt.entityLiving.worldObj.createExplosion(evt.entityLiving, evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ, 6f, flag);
                    }
                    else
                    {
                        evt.entityLiving.worldObj.createExplosion(evt.entityLiving, evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ, 3f, flag);
                    }

                }
            }
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // Setup command
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandICBM());

    }

}