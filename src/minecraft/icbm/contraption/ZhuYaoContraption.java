package icbm.contraption;

import icbm.api.ICBM;
import icbm.api.ICBMTab;
import icbm.core.ICBMPacketManager;
import icbm.core.ZhuYao;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.ItemElectric;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ZhuYaoContraption.NAME, name = ZhuYaoContraption.NAME, version = ICBM.VERSION, dependencies = "after:ICBM")
@NetworkMod(channels = ZhuYaoContraption.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = ICBMPacketManager.class)
public class ZhuYaoContraption
{
	public static final String NAME = ICBM.NAME + "|Contraption";
	public static final String CHANNEL = ICBM.NAME;

	@Instance(ZhuYaoContraption.NAME)
	public static ZhuYaoContraption instance;

	@SidedProxy(clientSide = "icbm.contraption.ClientProxy", serverSide = "icbm.contraption.CommonProxy")
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
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

		ICBM.CONFIGURATION.load();

		// Blocks
		bBuoLiPan = new BBuoLiPan(ICBM.CONFIGURATION.getBlock("BlockID2", ICBM.BLOCK_ID_PREFIX + 1).getInt());
		bBuoLiEnNiu = new BEnNiu(ICBM.CONFIGURATION.getBlock("BlockID7", ICBM.BLOCK_ID_PREFIX + 2).getInt());
		bYinGanQi = new BYinGanQi(ICBM.CONFIGURATION.getBlock("BlockID5", ICBM.BLOCK_ID_PREFIX + 5).getInt(), 7);
		bZha = new BZha(ICBM.CONFIGURATION.getBlock("BlockID8", ICBM.BLOCK_ID_PREFIX + 7).getInt(), 1);
		bYinXing = new BYinXing(ICBM.CONFIGURATION.getBlock("BlockID9", ICBM.BLOCK_ID_PREFIX + 8).getInt(), 11);

		// ITEMS
		itYao = new ItYao(ICBM.CONFIGURATION.getItem("ItemID3", ICBM.ITEM_ID_PREFIX + 2).getInt(), 2);
		itHuoLaunQi = new ItHuoLuanQi(ICBM.CONFIGURATION.getItem("ItemID10", ICBM.ITEM_ID_PREFIX + 9).getInt(), 7);
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
		GameRegistry.addRecipe(new ItemStack(bZha, 1, 2), new Object[] { "E", "S", 'E', ZhuYao.itLiu, 'S', bZha });

		// Camouflage
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bYinXing, 12), new Object[] { "WGW", "GCG", "WGW", 'C', "basicCircuit", 'G', Block.glass, 'W', Block.cloth }));

		// Tracker
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itGenZongQi), new Object[] { " Z ", "SBS", "SCS", 'Z', Item.compass, 'C', "basicCircuit", 'B', "battery", 'S', "ingotSteel" }));

		// Glass Pressure Plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoContraption.bBuoLiPan, 1, 0), new Object[] { "##", '#', Block.glass }));

		// Glass Button
		GameRegistry.addRecipe(new ItemStack(bBuoLiEnNiu, 2), new Object[] { "G", "G", 'G', Block.glass });

		this.proxy.init();
	}
}
