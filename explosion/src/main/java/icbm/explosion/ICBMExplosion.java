package icbm.explosion;

import icbm.Reference;
import icbm.Settings;
import icbm.TabICBM;
import icbm.core.ICBMCore;
import icbm.core.implement.IChunkLoadHandler;
import icbm.explosion.entities.EntityBombCart;
import icbm.explosion.entities.EntityExplosion;
import icbm.explosion.entities.EntityExplosive;
import icbm.explosion.entities.EntityGrenade;
import icbm.explosion.entities.EntityLightBeam;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.explosive.BlockExplosive;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.explosive.ItemBlockExplosive;
import icbm.explosion.items.ItemBombCart;
import icbm.explosion.items.ItemDefuser;
import icbm.explosion.items.ItemGrenade;
import icbm.explosion.items.ItemLaserDesignator;
import icbm.explosion.items.ItemMissile;
import icbm.explosion.items.ItemRadarGun;
import icbm.explosion.items.ItemRemoteDetonator;
import icbm.explosion.items.ItemRocketLauncher;
import icbm.explosion.machines.BlockICBMMachine;
import icbm.explosion.machines.BlockICBMMachine.MachineData;
import icbm.explosion.machines.BlockMissileAssembler;
import icbm.explosion.machines.ItemBlockMachine;
import icbm.explosion.machines.ItemBlockMissileAssembler;
import icbm.explosion.potion.ContagiousPoison;
import icbm.explosion.potion.PoisonContagion;
import icbm.explosion.potion.PoisonFrostBite;
import icbm.explosion.potion.PoisonToxin;
import icbm.sentry.gs.block.BlockLaserGate;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.api.CompatibilityModule;
import universalelectricity.api.vector.Vector3;
import calclavia.api.icbm.ExplosiveHelper;
import calclavia.api.icbm.explosion.ExplosionEvent.ExplosionConstructionEvent;
import calclavia.api.icbm.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import calclavia.api.icbm.explosion.ExplosionEvent.PreExplosionEvent;
import calclavia.lib.config.Config;
import calclavia.lib.flag.FlagRegistry;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.recipe.RecipeUtility;
import calclavia.lib.recipe.UniversalRecipe;
import calclavia.lib.utility.PotionUtility;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ICBMExplosion.ID, name = ICBMExplosion.NAME, version = Reference.VERSION, dependencies = "required-after:ICBM;after:ICBM|Sentry")
@NetworkMod(channels = ICBMExplosion.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class ICBMExplosion
{
    public static final String NAME = Reference.NAME + " Explosion";
    public static final String ID = Reference.NAME + "|Explosion";
    public static final String CHANNEL = Reference.NAME + "|E";

    @Instance(ID)
    public static ICBMExplosion instance;

    @Metadata(ID)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = "icbm.explosion.ClientProxy", serverSide = "icbm.explosion.CommonProxy")
    public static CommonProxy proxy;
    public static Item Du;
    public static final int ENTITY_ID_PREFIX = 50;

    /** Settings and Configurations */
    // Blocks
    public static Block blockExplosive;
    public static Block blockMachine;
    public static Block blockMissileAssembler;
    public static Block blockLaserGate;
    
    // Items
    public static Item itemMissile;

    public static Item itemDefuser;
    public static Item itemRadarGun;
    public static Item itemRemoteDetonator;
    public static Item itemLaserDesignator;
    public static Item itemRocketLauncher;

    public static Item itemGrenade;
    public static Item itemBombCart;

    public static final ContagiousPoison poisonous_potion = new ContagiousPoison("Chemical", 1, false);
    public static final ContagiousPoison contagios_potion = new ContagiousPoison("Contagious", 1, true);

    @Config(key = "Creepers Drop Sulfur", category = "Extras")
    public static boolean CREEPER_DROP_SULFER = true;
    @Config(key = "Creepers Blow up in Fire", category = "Extras")
    public static boolean CREEPER_BLOW_UP_IN_FIRE = true;

    @EventHandler
    // @Optional.Method(modid = ID)
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.instance().registerGuiHandler(this, proxy);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(proxy);
        FlagRegistry.registerFlag("ban_ICBM");

        blockExplosive = ICBMCore.contentRegistry.createBlock(BlockExplosive.class, ItemBlockExplosive.class);
        blockMachine = ICBMCore.contentRegistry.createBlock(BlockICBMMachine.class, ItemBlockMachine.class);
        blockMissileAssembler = ICBMCore.contentRegistry.createBlock(BlockMissileAssembler.class, ItemBlockMissileAssembler.class);
        blockLaserGate = ICBMCore.contentRegistry.createBlock(BlockLaserGate.class);
        
        // ITEMS
        itemMissile = ICBMCore.contentRegistry.createItem(ItemMissile.class);

        itemDefuser = ICBMCore.contentRegistry.createItem(ItemDefuser.class);
        itemRadarGun = ICBMCore.contentRegistry.createItem(ItemRadarGun.class);
        itemRemoteDetonator = ICBMCore.contentRegistry.createItem(ItemRemoteDetonator.class);
        itemLaserDesignator = ICBMCore.contentRegistry.createItem(ItemLaserDesignator.class);
        itemRocketLauncher = ICBMCore.contentRegistry.createItem(ItemRocketLauncher.class);

        itemGrenade = ICBMCore.contentRegistry.createItem(ItemGrenade.class);
        itemBombCart = ICBMCore.contentRegistry.createItem(ItemBombCart.class);

        /** Potion Effects */
        PoisonToxin.INSTANCE = new PoisonToxin(PotionUtility.getNextOptimalPotId(), true, 5149489, "toxin");
        PoisonContagion.INSTANCE = new PoisonContagion(PotionUtility.getNextOptimalPotId(), false, 5149489, "virus");
        PoisonFrostBite.INSTANCE = new PoisonFrostBite(PotionUtility.getNextOptimalPotId(), false, 5149489, "frostBite");
		if (!Loader.isModLoaded("ICBM|Sentry"))
			TabICBM.itemStack = new ItemStack(blockExplosive);

        /** Dispenser Handler */
        BlockDispenser.dispenseBehaviorRegistry.putObject(itemGrenade, new IBehaviorDispenseItem()
        {
            @Override
            public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
            {
                World world = blockSource.getWorld();

                if (!world.isRemote)
                {
                    int x = blockSource.getXInt();
                    int y = blockSource.getYInt();
                    int z = blockSource.getZInt();
                    EnumFacing enumFacing = EnumFacing.getFront(blockSource.getBlockMetadata());

                    EntityGrenade entity = new EntityGrenade(world, new Vector3(x, y, z), itemStack.getItemDamage());
                    entity.setThrowableHeading(enumFacing.getFrontOffsetX(), 0.10000000149011612D, enumFacing.getFrontOffsetZ(), 0.5F, 1.0F);
                    world.spawnEntityInWorld(entity);
                }

                itemStack.stackSize--;
                return itemStack;
            }
        });

        BlockDispenser.dispenseBehaviorRegistry.putObject(itemBombCart, new IBehaviorDispenseItem()
        {
            private final BehaviorDefaultDispenseItem defaultItemDispenseBehavior = new BehaviorDefaultDispenseItem();

            @Override
            public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
            {
                World world = blockSource.getWorld();

                if (!world.isRemote)
                {
                    int x = blockSource.getXInt();
                    int y = blockSource.getYInt();
                    int z = blockSource.getZInt();

                    EnumFacing var3 = EnumFacing.getFront(blockSource.getBlockMetadata());
                    World var4 = blockSource.getWorld();
                    double var5 = blockSource.getX() + var3.getFrontOffsetX() * 1.125F;
                    double var7 = blockSource.getY();
                    double var9 = blockSource.getZ() + var3.getFrontOffsetZ() * 1.125F;
                    int var11 = blockSource.getXInt() + var3.getFrontOffsetX();
                    int var12 = blockSource.getYInt();
                    int var13 = blockSource.getZInt() + var3.getFrontOffsetZ();
                    int var14 = var4.getBlockId(var11, var12, var13);
                    double var15;

                    if (BlockRailBase.isRailBlock(var14))
                    {
                        var15 = 0.0D;
                    }
                    else
                    {
                        if (var14 != 0 || !BlockRailBase.isRailBlock(var4.getBlockId(var11, var12 - 1, var13)))
                        {
                            return this.defaultItemDispenseBehavior.dispense(blockSource, itemStack);
                        }

                        var15 = -1.0D;
                    }

                    EntityBombCart var22 = new EntityBombCart(world, var5, var7 + var15, var9, itemStack.getItemDamage());
                    world.spawnEntityInWorld(var22);
                    world.playAuxSFX(1000, x, y, z, 0);
                }

                itemStack.stackSize--;
                return itemStack;
            }
        });

        /** Chunk loading handler. */
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new LoadingCallback()
        {
            @Override
            public void ticketsLoaded(List<Ticket> tickets, World world)
            {
                for (Ticket ticket : tickets)
                {
                    if (ticket.getEntity() instanceof IChunkLoadHandler)
                    {
                        ((IChunkLoadHandler) ticket.getEntity()).chunkLoaderInit(ticket);
                    }
                    else
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

        ExplosiveHelper.explosionManager = ExplosiveRegistry.class;

        proxy.preInit();
    }

    @EventHandler
    // @Optional.Method(modid = ID)
    public void init(FMLInitializationEvent evt)
    {
        Settings.setModMetadata(ID, NAME, metadata, Reference.NAME);

        EntityRegistry.registerGlobalEntityID(EntityExplosive.class, "ICBMExplosive", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityMissile.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityExplosion.class, "ICBMProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityLightBeam.class, "ICBMLightBeam", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityGrenade.class, "ICBMGrenade", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityBombCart.class, "ICBMChe", EntityRegistry.findGlobalUniqueEntityId());

        EntityRegistry.registerModEntity(EntityExplosive.class, "ICBMExplosive", ENTITY_ID_PREFIX, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityMissile.class, "ICBMMissile", ENTITY_ID_PREFIX + 1, this, 500, 1, true);
        EntityRegistry.registerModEntity(EntityExplosion.class, "ICBMProceduralExplosion", ENTITY_ID_PREFIX + 2, this, 100, 5, true);
        EntityRegistry.registerModEntity(EntityLightBeam.class, "ICBMLightBeam", ENTITY_ID_PREFIX + 4, this, 80, 5, true);
        EntityRegistry.registerModEntity(EntityGrenade.class, "ICBMGrenade", ENTITY_ID_PREFIX + 6, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityBombCart.class, "ICBMChe", ENTITY_ID_PREFIX + 8, this, 50, 4, true);

        proxy.init();
    }

    @EventHandler
    // @Optional.Method(modid = ID)
    public void postInit(FMLPostInitializationEvent event)
    {
        /** Add all Recipes */
        // Rocket Launcher
        GameRegistry.addRecipe(new ShapedOreRecipe(itemRocketLauncher, new Object[] { "SCR", "SB ", 'R', itemRadarGun, 'C', new ItemStack(blockMachine, 1, MachineData.CruiseLauncher.ordinal() + 6), 'B', Block.stoneButton, 'S', UniversalRecipe.PRIMARY_METAL.get() }));
        // Radar Gun
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRadarGun), new Object[] { "@#!", " $!", "  !", '@', Block.glass, '!', UniversalRecipe.PRIMARY_METAL.get(), '#', UniversalRecipe.CIRCUIT_T1.get(), '$', Block.stoneButton }));
        // Remote
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRemoteDetonator), new Object[] { "?@@", "@#$", "@@@", '@', UniversalRecipe.PRIMARY_METAL.get(), '?', Item.redstone, '#', UniversalRecipe.CIRCUIT_T2.get(), '$', Block.stoneButton }));
        // Laser Designator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemLaserDesignator), new Object[] { "!  ", " ? ", "  @", '@', itemRemoteDetonator, '?', UniversalRecipe.CIRCUIT_T3.get(), '!', itemRadarGun }));
        // Defuser
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemDefuser), new Object[] { "I  ", " W ", "  C", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'W', UniversalRecipe.WRENCH.get(), 'I', UniversalRecipe.WIRE.get() }));
        // Missile Launcher Platform
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 0), new Object[] { "! !", "!C!", "!!!", '!', UniversalRecipe.SECONDARY_METAL.get(), 'C', UniversalRecipe.CIRCUIT_T1.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 1), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(blockMachine, 1, 0), '!', UniversalRecipe.PRIMARY_METAL.get(), 'C', UniversalRecipe.CIRCUIT_T2.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 2), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(blockMachine, 1, 1), '!', UniversalRecipe.PRIMARY_PLATE.get(), 'C', UniversalRecipe.CIRCUIT_T3.get() }));
        // Missile Launcher Panel
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 3), new Object[] { "!!!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T1.get(), '!', Block.glass, '?', UniversalRecipe.WIRE.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 4), new Object[] { "!$!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T2.get(), '!', UniversalRecipe.PRIMARY_METAL.get(), '?', UniversalRecipe.WIRE.get(), '$', new ItemStack(blockMachine, 1, 3) }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 5), new Object[] { "!$!", "!#!", "!?!", '#', UniversalRecipe.CIRCUIT_T3.get(), '!', Item.ingotGold, '?', UniversalRecipe.WIRE.get(), '$', new ItemStack(blockMachine, 1, 4) }));
        // Missile Launcher Support Frame
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 6), new Object[] { "! !", "!!!", "! !", '!', UniversalRecipe.SECONDARY_METAL.get() }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 7), new Object[] { "! !", "!@!", "! !", '!', UniversalRecipe.PRIMARY_METAL.get(), '@', new ItemStack(blockMachine, 1, 6) }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 8), new Object[] { "! !", "!@!", "! !", '!', UniversalRecipe.PRIMARY_PLATE.get(), '@', new ItemStack(blockMachine, 1, 7) }));
        // Radar Station
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 9), new Object[] { "?@?", " ! ", "!#!", '@', CompatibilityModule.getItemWithCharge(new ItemStack(itemRadarGun), 0), '!', UniversalRecipe.PRIMARY_PLATE.get(), '#', UniversalRecipe.CIRCUIT_T1.get(), '?', Item.ingotGold }));
        // EMP Tower
        RecipeUtility.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 10), new Object[] { "?W?", "@!@", "?#?", '?', UniversalRecipe.PRIMARY_PLATE.get(), '!', UniversalRecipe.CIRCUIT_T3.get(), '@', UniversalRecipe.BATTERY_BOX.get(), '#', UniversalRecipe.MOTOR.get(), 'W', UniversalRecipe.WIRE.get() }), "EMP Tower", Settings.CONFIGURATION, true);
        // Cruise Launcher
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 11), new Object[] { "?! ", "@@@", '@', UniversalRecipe.PRIMARY_PLATE.get(), '!', new ItemStack(blockMachine, 1, 2), '?', new ItemStack(blockMachine, 1, 8) }));
        // Missile Coordinator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockMachine, 1, 12), new Object[] { "R R", "SCS", "SSS", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'S', UniversalRecipe.PRIMARY_PLATE.get(), 'R', itemRemoteDetonator }));
        // Missile module
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), new Object[] { " @ ", "@#@", "@?@", '@', UniversalRecipe.PRIMARY_METAL.get(), '?', Item.flintAndSteel, '#', UniversalRecipe.CIRCUIT_T1.get() }));
        // Homing
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.homing.getID()), new Object[] { " B ", " C ", "BMB", 'M', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), 'C', UniversalRecipe.CIRCUIT_T1.get(), 'B', UniversalRecipe.SECONDARY_METAL.get() }));
        // Anti-ballistic
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.antiBallistic.getID()), new Object[] { "!", "?", "@", '@', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), '?', new ItemStack(blockExplosive, 1, 0), '!', UniversalRecipe.CIRCUIT_T1.get() }));
        // Cluster
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.cluster.getID()), new Object[] { " ! ", " ? ", "!@!", '@', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), '?', Explosive.fragmentation.getItemStack(), '!', new ItemStack(itemMissile, 1, 0) }));
        // Nuclear Cluster
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMissile, 1, Explosive.nuclearCluster.getID()), new Object[] { " N ", "NCN", 'C', new ItemStack(itemMissile, 1, Explosive.cluster.getID()), 'N', Explosive.nuclear.getItemStack() }));

        // Add all explosive recipes.
        if (!Loader.isModLoaded("AtomicScience")) //TODO ? static id
            OreDictionary.registerOre("antimatterGram", new ItemStack(397, 1, 1));

        for (Explosive explosive : ExplosiveRegistry.getExplosives())
        {
            explosive.init();
            // Missile
            RecipeUtility.addRecipe(new ShapelessOreRecipe(new ItemStack(itemMissile, 1, explosive.getID()), new Object[] { new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), new ItemStack(blockExplosive, 1, explosive.getID()) }), explosive.getUnlocalizedName() + " Missile", Settings.CONFIGURATION, true);
            if (explosive.getTier() < 2)
            {
                // Grenade
                RecipeUtility.addRecipe(new ShapedOreRecipe(new ItemStack(itemGrenade, 1, explosive.getID()), new Object[] { "?", "@", '@', new ItemStack(blockExplosive, 1, explosive.getID()), '?', Item.silk }), explosive.getUnlocalizedName() + " Grenade", Settings.CONFIGURATION, true);
            }
            if (explosive.getTier() < 3)
            {
                // Minecart
                RecipeUtility.addRecipe(new ShapedOreRecipe(new ItemStack(itemBombCart, 1, explosive.getID()), new Object[] { "?", "@", '?', new ItemStack(blockExplosive, 1, explosive.getID()), '@', Item.minecartEmpty }), explosive.getUnlocalizedName() + " Minecart", Settings.CONFIGURATION, true);
            }
        }
    }

    @ForgeSubscribe
    public void enteringChunk(EnteringChunk evt)
    {
        if (evt.entity instanceof EntityMissile)
        {
            ((EntityMissile) evt.entity).updateLoadChunk(evt.newChunkX, evt.newChunkZ);
        }
    }

    @ForgeSubscribe
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

    @ForgeSubscribe
    public void creeperDropEvent(LivingDropsEvent evt)
    {
        if (evt.entityLiving instanceof EntityCreeper)
        {
            if (CREEPER_DROP_SULFER)
            {
                evt.entityLiving.dropItem(ICBMCore.itemSulfurDust.itemID, 1 + evt.entityLiving.worldObj.rand.nextInt(6));
            }
        }
    }

    @ForgeSubscribe
    public void preDetonationEvent(ExplosivePreDetonationEvent evt)
    {
        if (FlagRegistry.getModFlag() != null && evt.explosion instanceof Explosive)
        {
            if (((Explosive) evt.explosion).isBannedInRegion(evt.world, evt.x, evt.y, evt.z))
            {
                ICBMCore.LOGGER.fine("ICBM prevented explosive:" + evt.x + ", " + evt.y + "," + evt.z);
                evt.setCanceled(true);
            }
        }
    }

    @ForgeSubscribe
    public void preConstructionEvent(ExplosionConstructionEvent evt)
    {
        if (FlagRegistry.getModFlag() != null && evt.iExplosion instanceof Explosive)
        {
            if (((Explosive) evt.iExplosion).isBannedInRegion(evt.world, evt.x, evt.y, evt.z))
            {
                ICBMCore.LOGGER.fine("ICBM prevented explosive:" + evt.x + ", " + evt.y + "," + evt.z);
                evt.setCanceled(true);
            }
        }
    }

    @ForgeSubscribe
    public void preExplosionEvent(PreExplosionEvent evt)
    {
        if (FlagRegistry.getModFlag() != null && evt.iExplosion instanceof Explosive)
        {
            if (((Explosive) evt.iExplosion).isBannedInRegion(evt.world, evt.x, evt.y, evt.z))
            {
                ICBMCore.LOGGER.fine("ICBM prevented explosive:" + evt.x + ", " + evt.y + "," + evt.z);
                evt.setCanceled(true);
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
