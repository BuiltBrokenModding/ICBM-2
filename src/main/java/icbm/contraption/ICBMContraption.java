package icbm.contraption;

import icbm.Reference;
import icbm.contraption.block.BlockCamouflage;
import icbm.contraption.block.BlockConcrete;
import icbm.contraption.block.BlockGlassButton;
import icbm.contraption.block.BlockGlassPressurePlate;
import icbm.contraption.block.BlockProcimityDetector;
import icbm.contraption.block.BlockReinforcedGlass;
import icbm.contraption.block.BlockSpikes;
import icbm.core.CreativeTabICBM;
import icbm.core.Settings;
import icbm.core.ICBMCore;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.api.item.ItemElectric;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.item.ItemBlockHolder;
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

@Mod(modid = ICBMContraption.NAME, name = ICBMContraption.NAME, version = Reference.VERSION, dependencies = "after:AtomicScience", useMetadata = true)
@NetworkMod(channels = ICBMContraption.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class ICBMContraption extends ICBMCore
{
	public static final String NAME = Reference.NAME + "|Contraption";
	public static final String CHANNEL = Reference.NAME + "|C";

	@Instance(NAME)
	public static ICBMContraption instance;

	@Metadata(NAME)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "icbm.contraption.ClientProxy", serverSide = "icbm.contraption.CommonProxy")
	public static CommonProxy proxy;

	// Blocks
	public static Block blockGlassPlate, blockGlassButton, blockProximityDetector, blockSpikes,
			blockCamo, blockConcrete, blockReinforcedGlass;

	// Items
	public static Item itemAntidote;
	public static ItemElectric itemSignalDisrupter;
	public static ItemElectric itemTracker;

	@Override
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		NetworkRegistry.instance().registerGuiHandler(this, ICBMContraption.proxy);

		Settings.CONFIGURATION.load();

		// Blocks
		blockGlassPlate = new BlockGlassPressurePlate(Settings.CONFIGURATION.getBlock("Glass Pressure Plate", Settings.getNextBlockID()).getInt());
		blockGlassButton = new BlockGlassButton(Settings.CONFIGURATION.getBlock("Glass Button", Settings.getNextBlockID()).getInt());
		blockProximityDetector = new BlockProcimityDetector(Settings.getNextBlockID());
		blockSpikes = new BlockSpikes(Settings.getNextBlockID());
		blockCamo = new BlockCamouflage(Settings.getNextBlockID());
		blockConcrete = new BlockConcrete(Settings.getNextBlockID());
		blockReinforcedGlass = new BlockReinforcedGlass(Settings.getNextBlockID());

		// ITEMS
		itemAntidote = new ItemAntidote(Settings.CONFIGURATION.getItem("ItemAntidote", Settings.getNextItemID()).getInt());
		itemSignalDisrupter = new ItemSignalDisrupter(Settings.CONFIGURATION.getItem("ItemSignalDisrupter", Settings.getNextItemID() + 9).getInt());
		itemTracker = new ItemTracker(Settings.CONFIGURATION.getItem("ItemTracker", Settings.getNextItemID()).getInt());

		Settings.CONFIGURATION.save();

		CreativeTabICBM.itemStack = new ItemStack(blockProximityDetector);

		// -- Registering Blocks
		GameRegistry.registerBlock(blockGlassPlate, "blockGlassPlate");
		GameRegistry.registerBlock(blockGlassButton, "blockGlassButton");
		GameRegistry.registerBlock(blockProximityDetector, "blockProximityDetector");
		GameRegistry.registerBlock(blockCamo, "blockCamo");
		GameRegistry.registerBlock(blockReinforcedGlass, "blockReinforcedGlass");
		GameRegistry.registerBlock(blockSpikes, ItemblockSpikes.class, "blockSpikes");
		GameRegistry.registerBlock(blockConcrete, ItemBlockHolder.class, "blockConcrete");

		ICBMContraption.proxy.preInit();
	}

	@EventHandler
	public void load(FMLInitializationEvent evt)
	{
		super.init(evt);
		ICBMCore.setModMetadata(NAME, metadata);
	}

	@Override
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

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

		ICBMContraption.proxy.init();
	}

	@Override
	protected String getChannel()
	{
		return CHANNEL;
	}
}
