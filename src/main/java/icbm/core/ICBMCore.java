package icbm.core;

import icbm.Reference;
import icbm.contraption.ItemAntidote;
import icbm.contraption.ItemBlockSpikes;
import icbm.contraption.ItemSignalDisrupter;
import icbm.contraption.ItemTracker;
import icbm.contraption.block.BlockCamouflage;
import icbm.contraption.block.BlockConcrete;
import icbm.contraption.block.BlockGlassButton;
import icbm.contraption.block.BlockGlassPressurePlate;
import icbm.contraption.block.BlockProximityDetector;
import icbm.contraption.block.BlockReinforcedGlass;
import icbm.contraption.block.BlockSpikes;
import icbm.core.prefab.item.ItemICBMBase;

import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import universalelectricity.api.item.ItemElectric;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.network.PacketPlayerItem;
import calclavia.lib.network.PacketTile;
import calclavia.lib.ore.OreGenBase;
import calclavia.lib.ore.OreGenerator;
import calclavia.lib.prefab.item.ItemBlockMetadata;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Main class for ICBM core to run on. The core will need to be initialized by each ICBM module.
 * 
 * @author Calclavia
 */
@Mod(modid = Reference.NAME, name = Reference.NAME, version = Reference.VERSION, dependencies = "after:AtomicScience")
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
	public static ItemElectric itemSignalDisrupter;
	public static ItemElectric itemTracker;

	public static Block blockSulfurOre, blockRadioactive;

	public static Item itemSulfurDust, itemPoisonPowder;

	public static OreGenBase sulfurGenerator;

	private static boolean isPreInit, isInit, isPostInit;

	public static final Logger LOGGER = Logger.getLogger(Reference.NAME);

	public static final PacketTile PACKET_TILE = new PacketTile(Reference.CHANNEL);
	public static final PacketPlayerItem PACKET_ITEM = new PacketPlayerItem(Reference.CHANNEL);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(this, proxy);

		Modstats.instance().getReporter().registerMod(INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);

		LOGGER.fine("Loaded " + LanguageUtility.loadLanguages(icbm.Reference.LANGUAGE_PATH, icbm.Reference.LANGUAGES) + " languages.");

		Settings.initiate();
		Settings.CONFIGURATION.load();

		// Blocks
		blockSulfurOre = new BlockSulfureOre(Settings.CONFIGURATION.getBlock("sulfureOre", Settings.getNextBlockID()).getInt());
		blockGlassPlate = new BlockGlassPressurePlate(Settings.CONFIGURATION.getBlock("Glass Pressure Plate", Settings.getNextBlockID()).getInt());
		blockGlassButton = new BlockGlassButton(Settings.CONFIGURATION.getBlock("Glass Button", Settings.getNextBlockID()).getInt());
		blockProximityDetector = new BlockProximityDetector(Settings.getNextBlockID());
		blockSpikes = new BlockSpikes(Settings.getNextBlockID());
		blockCamo = new BlockCamouflage(Settings.getNextBlockID());
		blockConcrete = new BlockConcrete(Settings.getNextBlockID());
		blockReinforcedGlass = new BlockReinforcedGlass(Settings.getNextBlockID());

		// ITEMS
		itemPoisonPowder = new ItemICBMBase(Settings.CONFIGURATION.getItem("itemPoisonPowder", Settings.getNextItemID()).getInt(), "poisonPowder");
		itemSulfurDust = new ItemICBMBase(Settings.CONFIGURATION.getItem("itemSulfurDust", Settings.getNextItemID()).getInt(), "sulfur");
		itemAntidote = new ItemAntidote(Settings.CONFIGURATION.getItem("ItemAntidote", Settings.getNextItemID()).getInt());
		itemSignalDisrupter = new ItemSignalDisrupter(Settings.CONFIGURATION.getItem("ItemSignalDisrupter", Settings.getNextItemID() + 9).getInt());
		itemTracker = new ItemTracker(Settings.CONFIGURATION.getItem("ItemTracker", Settings.getNextItemID()).getInt());

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

		Settings.CONFIGURATION.save();

		CreativeTabICBM.itemStack = new ItemStack(blockProximityDetector);

		// -- Registering Blocks
		GameRegistry.registerBlock(blockSulfurOre, "blockSulferOre");
		GameRegistry.registerBlock(blockGlassPlate, "blockGlassPlate");
		GameRegistry.registerBlock(blockGlassButton, "blockGlassButton");
		GameRegistry.registerBlock(blockProximityDetector, "blockProximityDetector");
		GameRegistry.registerBlock(blockCamo, "blockCamo");
		GameRegistry.registerBlock(blockReinforcedGlass, "blockReinforcedGlass");
		GameRegistry.registerBlock(blockSpikes, ItemBlockSpikes.class, "blockSpikes");
		GameRegistry.registerBlock(blockConcrete, ItemBlockMetadata.class, "blockConcrete");

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ICBMCore.setModMetadata(Reference.NAME, metadata);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{

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
		GameRegistry.addRecipe(new ItemStack(blockSpikes, 1, 1), new Object[] { "E", "S", 'E', ICBMCore.itemPoisonPowder, 'S', blockSpikes });
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSpikes, 1, 2), new Object[] { "E", "S", 'E', "dustSulfur", 'S', blockSpikes }));

		// Camouflage
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCamo, 12), new Object[] { "WGW", "G G", "WGW", 'G', Block.vine, 'W', Block.cloth }));

		// Tracker
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemTracker), new Object[] { " Z ", "SBS", "SCS", 'Z', Item.compass, 'C', "circuitBasic", 'B', "battery", 'S', Item.ingotIron }));

		// Glass Pressure Plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockGlassPlate, 1, 0), new Object[] { "##", '#', Block.glass }));

		// Glass Button
		GameRegistry.addRecipe(new ItemStack(blockGlassButton, 2), new Object[] { "G", "G", 'G', Block.glass });

		// Proximity Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(blockProximityDetector, new Object[] { "SSS", "S?S", "SSS", 'S', Item.ingotIron, '?', itemTracker }));

		// Signal Disrupter
		GameRegistry.addRecipe(new ShapedOreRecipe(itemSignalDisrupter, new Object[] { "WWW", "SCS", "SSS", 'S', Item.ingotIron, 'C', "circuitBasic", 'W', "wireCopper" }));

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
	}

	public static void setModMetadata(String id, ModMetadata metadata)
	{
		metadata.modId = id;
		metadata.name = Reference.NAME;
		metadata.description = "ICBM is a Minecraft Mod that introduces intercontinental ballistic missiles to Minecraft. But the fun doesn't end there! This mod also features many different explosives, missiles and machines classified in three different tiers. If strategic warfare, carefully coordinated airstrikes, messing with matter and general destruction are up your alley, then this mod is for you!";
		metadata.url = "http://www.calclavia.com/icbm/";
		metadata.logoFile = "/icbm_logo.png";
		metadata.version = Reference.VERSION;
		metadata.authorList = Arrays.asList(new String[] { "Calclavia" });
		metadata.credits = "Please visit the website.";
		metadata.autogenerated = false;
	}
}