package icbm.wanyi;

import icbm.api.ICBM;
import icbm.api.ICBMTab;
import icbm.core.ItGenZongQi;
import icbm.core.ZhuYao;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.core.item.ItemElectric;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ZhuYaoWanYi.NAME, name = ZhuYaoWanYi.NAME, version = ICBM.VERSION, dependencies = "after:BasicComponents;after:AtomicScience")
@NetworkMod(channels = ZhuYaoWanYi.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = WanYiPacketGuanLi.class)
public class ZhuYaoWanYi
{
	public static final String NAME = ICBM.NAME + "|Contraption";
	public static final String CHANNEL = ICBM.NAME + "|C";

	@Instance(ZhuYaoWanYi.NAME)
	public static ZhuYaoWanYi instance;

	@SidedProxy(clientSide = "icbm.wanyi.ClientProxy", serverSide = "icbm.wanyi.CommonProxy")
	public static CommonProxy proxy;

	// Blocks
	public static Block bBuoLiPan;
	public static BEnNiu bBuoLiEnNiu;
	public static Block bYinGanQi;
	public static Block bZha;
	public static Block bYinXing;

	// Items
	public static Item itYao;
	public static ItemElectric itHuoLaunQi;
	public static ItemElectric itGenZongQi;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		ZhuYao.INSTANCE.init();
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

		ICBM.CONFIGURATION.load();

		// Blocks
		bBuoLiPan = new BBuoLiPan(ICBM.CONFIGURATION.getBlock("BlockID2", ICBM.BLOCK_ID_PREFIX + 1).getInt());
		bBuoLiEnNiu = new BEnNiu(ICBM.CONFIGURATION.getBlock("BlockID7", ICBM.BLOCK_ID_PREFIX + 2).getInt());
		bYinGanQi = new BYinGanQi(ICBM.CONFIGURATION.getBlock("BlockID5", ICBM.BLOCK_ID_PREFIX + 5).getInt());
		bZha = new BZha(ICBM.CONFIGURATION.getBlock("BlockID8", ICBM.BLOCK_ID_PREFIX + 7).getInt());
		bYinXing = new BYinXing(ICBM.CONFIGURATION.getBlock("BlockID9", ICBM.BLOCK_ID_PREFIX + 8).getInt(), 11);

		//Concrete
		
		// ITEMS
		itYao = new ItYao(ICBM.CONFIGURATION.getItem("ItemID3", ICBM.ITEM_ID_PREFIX + 2).getInt());
		itHuoLaunQi = new ItHuoLuanQi(ICBM.CONFIGURATION.getItem("ItemID10", ICBM.ITEM_ID_PREFIX + 9).getInt());
		itGenZongQi = new ItGenZongQi(ICBM.CONFIGURATION.getItem("ItemID11", ICBM.ITEM_ID_PREFIX + 10).getInt());

		ICBM.CONFIGURATION.save();

		ICBMTab.itemStack = new ItemStack(bYinGanQi);

		// -- Registering Blocks
		GameRegistry.registerBlock(bBuoLiPan, "bBuoLiPan");
		GameRegistry.registerBlock(bBuoLiEnNiu, "bBuoLiEnNiu");
		GameRegistry.registerBlock(bYinGanQi, "bYinGanQi");
		GameRegistry.registerBlock(bYinXing, "bYinXing");
		GameRegistry.registerBlock(bZha, IBZha.class, "bZha");

		this.proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		/**
		 * Add all Recipes
		 */
		// Spikes
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 6), new Object[] { "CCC", "BBB", 'C', Block.cactus, 'B', "ingotBronze" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 6), new Object[] { "CCC", "BBB", 'C', Block.cactus, 'B', Item.ingotIron }));
		GameRegistry.addRecipe(new ItemStack(bZha, 1, 1), new Object[] { "E", "S", 'E', ZhuYao.itDu, 'S', bZha });
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 1, 2), new Object[] { "E", "S", 'E', "dustSulfur", 'S', bZha }));

		// Camouflage
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bYinXing, 12), new Object[] { "WGW", "GCG", "WGW", 'C', "basicCircuit", 'G', Block.glass, 'W', Block.cloth }));

		// Tracker
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itGenZongQi), new Object[] { " Z ", "SBS", "SCS", 'Z', Item.compass, 'C', "basicCircuit", 'B', "battery", 'S', "ingotSteel" }));

		// Glass Pressure Plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoWanYi.bBuoLiPan, 1, 0), new Object[] { "##", '#', Block.glass }));

		// Glass Button
		GameRegistry.addRecipe(new ItemStack(bBuoLiEnNiu, 2), new Object[] { "G", "G", 'G', Block.glass });

		// Proximity Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(bYinGanQi, new Object[] { "SSS", "S?S", "SSS", 'S', "ingotSteel", '?', ElectricItemHelper.getUncharged(itGenZongQi) }));

		// Signal Disrupter
		GameRegistry.addRecipe(new ShapedOreRecipe(itHuoLaunQi, new Object[] { "WWW", "SCS", "SSS", 'S', "ingotSteel", 'C', "basicCircuit", 'W', "copperWire" }));

		// Antidote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itYao, 6), new Object[] { "@@@", "@@@", "@@@", '@', Item.pumpkinSeeds }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itYao), new Object[] { "@@@", "@@@", "@@@", '@', Item.seeds }));

		this.proxy.init();
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		ZhuYao.INSTANCE.serverStarting(event);
	}
}
