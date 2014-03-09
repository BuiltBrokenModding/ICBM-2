package icbm.core;

import calclavia.components.CalclaviaLoader;
import calclavia.lib.configurable.ConfigHandler;
import calclavia.lib.content.ContentRegistry;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.network.PacketPlayerItem;
import calclavia.lib.network.PacketTile;
import calclavia.lib.ore.OreGenBase;
import calclavia.lib.ore.OreGenerator;
import calclavia.lib.prefab.item.ItemBlockMetadata;
import calclavia.lib.recipe.UniversalRecipe;
import calclavia.lib.utility.LanguageUtility;
import calclavia.lib.utility.ModUtility;
import cpw.mods.fml.common.FMLCommonHandler;
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
import cpw.mods.fml.common.registry.GameRegistry;
import icbm.Reference;
import icbm.contraption.*;
import icbm.contraption.block.*;
import icbm.explosion.CommandICBM;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import java.util.logging.Logger;

/**
 * Main class for ICBM core to run on. The core will need to be initialized by each ICBM module.
 * 
 * @author Calclavia
 */
@Mod(modid = Reference.NAME, name = Reference.NAME, version = Reference.VERSION, dependencies = "after:AtomicScience;required-after:CalclaviaCore")
@NetworkMod(channels = Reference.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
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
	public static Block blockGlassPlate, blockGlassButton, blockProximityDetector, blockSpikes,
			blockCamo, blockConcrete, blockReinforcedGlass;

	// Items
	public static Item itemAntidote;
	public static Item itemSignalDisrupter;
	public static Item itemTracker;

	public static Block blockSulfurOre, blockRadioactive;

	public static Item itemSulfurDust, itemPoisonPowder;

	public static OreGenBase sulfurGenerator;

	private static boolean isPreInit, isInit, isPostInit;

	public static final Logger LOGGER = Logger.getLogger(Reference.NAME);

	public static final PacketTile PACKET_TILE = new PacketTile(Reference.CHANNEL);
	public static final PacketPlayerItem PACKET_ITEM = new PacketPlayerItem(Reference.CHANNEL);

	public static final ContentRegistry contentRegistry = new ContentRegistry(Settings.CONFIGURATION, Settings.idManager, Reference.NAME).setPrefix(Reference.PREFIX).setTab(TabICBM.INSTANCE);

    public ICBMCore()
    {
        ModUtility.removeMod("ICBM|Sentry", Settings.CONFIGURATION.get("Child_Mods", "Enable_Sentry", false).getBoolean(false));
        ModUtility.removeMod("ICBM|Explosion", Settings.CONFIGURATION.get("Child_Mods", "Enable_Explosion", false).getBoolean(false));
    }

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

		NetworkRegistry.instance().registerGuiHandler(this, proxy);

		Modstats.instance().getReporter().registerMod(INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);

		LOGGER.fine("Loaded " + LanguageUtility.loadLanguages(icbm.Reference.LANGUAGE_PATH, icbm.Reference.LANGUAGES) + " languages.");

		//Settings.initiate();
		Settings.CONFIGURATION.load();

		// String submodCategory = "Sub_Modules";
		// ProxyHandler.applyModule(ICBMSentry.class, Settings.CONFIGURATION.get(submodCategory,
		// ICBMSentry.class.getSimpleName(), false).getBoolean(false));

		CalclaviaLoader.blockMulti.setTextureName(Reference.PREFIX + "machine");

		// Blocks
		blockSulfurOre = contentRegistry.createBlock(BlockSulfurOre.class);
		blockGlassPlate = contentRegistry.createBlock(BlockGlassPressurePlate.class);
		blockGlassButton = contentRegistry.createBlock(BlockGlassButton.class);
		blockProximityDetector = contentRegistry.createTile(BlockProximityDetector.class, TileProximityDetector.class);
		blockSpikes = contentRegistry.createBlock(BlockSpikes.class, ItemBlockMetadata.class);
		blockCamo = contentRegistry.createBlock(BlockCamouflage.class);
		blockConcrete = contentRegistry.createBlock(BlockConcrete.class, ItemBlockMetadata.class);
		blockReinforcedGlass = contentRegistry.createBlock(BlockReinforcedGlass.class, ItemBlockMetadata.class);

		// ITEMS
		itemPoisonPowder = contentRegistry.createItem(ItemPoisonPowder.class);
		itemSulfurDust = contentRegistry.createItem(ItemSulfurDust.class);
		itemAntidote = contentRegistry.createItem(ItemAntidote.class);
		itemSignalDisrupter = contentRegistry.createItem(ItemSignalDisrupter.class);
		itemTracker = contentRegistry.createItem(ItemTracker.class);

		sulfurGenerator = new OreGeneratorICBM("Sulfur Ore", "oreSulfur", new ItemStack(blockSulfurOre), 0, 40, 20, 4).enable(Settings.CONFIGURATION);

		/** Check for existence of radioactive block. If it does not exist, then create it. */
		if (OreDictionary.getOres("blockRadioactive").size() > 0)
		{
			blockRadioactive = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
			LOGGER.fine("Detected radioative block from another mod, utilizing it.");
		}
		else
		{
			blockRadioactive = Block.mycelium;
		}

		/** Decrease Obsidian Resistance */
		Block.obsidian.setResistance(Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45).getInt(45));
		LOGGER.fine("Changed obsidian explosive resistance to: " + Block.obsidian.getExplosionResistance(null));

		OreDictionary.registerOre("dustSulfur", itemSulfurDust);
		OreGenerator.addOre(sulfurGenerator);

		TabICBM.itemStack = new ItemStack(blockProximityDetector);

		proxy.preInit();
		LOGGER.info("Calling preinit for submodules");
		ProxyHandler.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		Settings.setModMetadata(Reference.NAME, Reference.NAME, metadata);
		LOGGER.info("Calling init for submodules");
		ProxyHandler.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
        try
        {
            ConfigHandler.configure(Settings.CONFIGURATION, "icbm");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Settings.CONFIGURATION.save();
		/** LOAD. */

		// Sulfur
		GameRegistry.addSmelting(blockSulfurOre.blockID, new ItemStack(itemSulfurDust, 4), 0.8f);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 3), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', Item.coal }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 3), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', new ItemStack(Item.coal, 1, 1) }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Block.tnt, new Object[] { "@@@", "@R@", "@@@", '@', Item.gunpowder, 'R', Item.redstone }));

		// Poison Powder
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemPoisonPowder, 3), new Object[] { Item.spiderEye, Item.rottenFlesh }));
		/** Add all Recipes */
		// Spikes
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSpikes, 6), new Object[] { "CCC", "BBB", 'C', Block.cactus, 'B', Item.ingotIron }));
		GameRegistry.addRecipe(new ItemStack(blockSpikes, 1, 1), new Object[] { "E", "S", 'E', itemPoisonPowder, 'S', blockSpikes });
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSpikes, 1, 2), new Object[] { "E", "S", 'E', itemSulfurDust, 'S', blockSpikes }));

		// Camouflage
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCamo, 12), new Object[] { "WGW", "G G", "WGW", 'G', Block.vine, 'W', Block.cloth }));

		// Tracker
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTracker), new Object[] { " Z ", "SBS", "SCS", 'Z', Item.compass, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'B', UniversalRecipe.BATTERY.get(), 'S', Item.ingotIron }));

		// Glass Pressure Plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockGlassPlate, 1, 0), new Object[] { "##", '#', Block.glass }));

		// Glass Button
		GameRegistry.addRecipe(new ItemStack(blockGlassButton, 2), new Object[] { "G", "G", 'G', Block.glass });

		// Proximity Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockProximityDetector, new Object[] { "SSS", "S?S", "SSS", 'S', Item.ingotIron, '?', itemTracker }));

		// Signal Disrupter
		GameRegistry.addRecipe(new ShapedOreRecipe(itemSignalDisrupter, new Object[] { "WWW", "SCS", "SSS", 'S', Item.ingotIron, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'W', UniversalRecipe.WIRE.get() }));

		// Antidote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAntidote, 6), new Object[] { "@@@", "@@@", "@@@", '@', Item.pumpkinSeeds }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAntidote), new Object[] { "@@@", "@@@", "@@@", '@', Item.seeds }));

		// Concrete
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConcrete, 8, 0), new Object[] { "SGS", "GWG", "SGS", 'G', Block.gravel, 'S', Block.sand, 'W', Item.bucketWater }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConcrete, 8, 1), new Object[] { "COC", "OCO", "COC", 'C', new ItemStack(blockConcrete, 1, 0), 'O', Block.obsidian }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockConcrete, 8, 2), new Object[] { "COC", "OCO", "COC", 'C', new ItemStack(blockConcrete, 1, 1), 'O', Item.ingotIron }));

		// Reinforced Glass
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockReinforcedGlass, 8), new Object[] { "IGI", "GIG", "IGI", 'G', Block.glass, 'I', Item.ingotIron }));

		proxy.init();

		LOGGER.info("Calling postInit for submodules");
		ProxyHandler.postInit(event);
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