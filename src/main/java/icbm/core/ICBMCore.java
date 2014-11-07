package icbm.core;

import icbm.Reference;
import icbm.Settings;
import icbm.TabICBM;
import icbm.core.blocks.*;
import icbm.core.compat.Waila;
import icbm.core.entity.EntityFlyingBlock;
import icbm.core.entity.EntityFragments;
import icbm.core.items.ItemAntidote;
import icbm.core.items.ItemComputer;
import icbm.core.items.ItemPoisonPowder;
import icbm.core.items.ItemSignalDisrupter;
import icbm.core.items.ItemSulfurDust;
import icbm.core.items.ItemTracker;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;
import resonant.content.loader.ModManager;
import resonant.content.prefab.itemblock.ItemBlockMetadata;
import resonant.lib.config.ConfigHandler;
import resonant.lib.loadable.LoadableHandler;
import resonant.lib.ore.OreGenerator;
import resonant.lib.recipe.UniversalRecipe;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

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

    // Blocks
    public static Block blockGlassPlate, blockGlassButton, blockProximityDetector, blockSpikes, blockCamo, blockConcrete, blockReinforcedGlass;

    // Items
    public static Item itemAntidote;
    public static Item itemSignalDisrupter;
    public static Item itemTracker;
    public static Item itemHackingComputer;

    public static Block blockSulfurOre, blockRadioactive, blockCombatRail, blockBox;

    public static Item itemSulfurDust, itemPoisonPowder;

    public static OreGenerator sulfurGenerator;

    public static final Logger LOGGER = Logger.getLogger(Reference.NAME);

    public static final ModManager contentRegistry = new ModManager().setPrefix(Reference.PREFIX).setTab(TabICBM.INSTANCE);

    private LoadableHandler modproxies = new LoadableHandler();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        Modstats.instance().getReporter().registerMod(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);

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
        //blockBox = contentRegistry.newBlock(TileBox.class); TODO Enable, disabled as to allow to release a stable ICBM for 1.6

        // ITEMS
        itemPoisonPowder = contentRegistry.newItem(ItemPoisonPowder.class);
        itemSulfurDust = contentRegistry.newItem(ItemSulfurDust.class);
        itemAntidote = contentRegistry.newItem(ItemAntidote.class);
        itemSignalDisrupter = contentRegistry.newItem(ItemSignalDisrupter.class);
        itemTracker = contentRegistry.newItem(ItemTracker.class);
        itemHackingComputer = contentRegistry.newItem(ItemComputer.class);

        //sulfurGenerator = new OreGenerator("Sulfur Ore", "oreSulfur", new ItemStack(blockSulfurOre), 0, 40, 20, 4);

        /** Check for existence of radioactive block. If it does not exist, then create it. */
        if (OreDictionary.getOres("blockRadioactive").size() > 0)
        {
            blockRadioactive = Block.getBlockFromItem(OreDictionary.getOres("blockRadioactive").get(0).getItem());
            LOGGER.fine("Detected radioative block from another mod, utilizing it.");
        }
        else
        {
            blockRadioactive = Blocks.mycelium;
        }

        /** Decrease Obsidian Resistance */
        Blocks.obsidian.setResistance(Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45).getInt(45));
        LOGGER.fine("Changed obsidian explosive resistance to: " + Blocks.obsidian.getExplosionResistance(null));

        OreDictionary.registerOre("dustSulfur", new ItemStack(itemSulfurDust, 1, 0));
        OreDictionary.registerOre("dustSaltpeter", new ItemStack(itemSulfurDust, 1, 1));
        //OreGenerator.addOre(sulfurGenerator);
        if (!Loader.isModLoaded("ICBM|Sentry") && !Loader.isModLoaded("ICBM|Explosion"))
            TabICBM.itemStack = new ItemStack(blockProximityDetector);

        proxy.preInit();
        modproxies.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Settings.setModMetadata(Reference.NAME, Reference.NAME, metadata);

        EntityRegistry.registerGlobalEntityID(EntityFlyingBlock.class, "ICBMGravityBlock", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityFragments.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());

        EntityRegistry.registerModEntity(EntityFlyingBlock.class, "ICBMGravityBlock", 0, this, 50, 15, true);
        EntityRegistry.registerModEntity(EntityFragments.class, "ICBMFragment", 1, this, 40, 8, true);

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

        modproxies.postInit();
    }

}