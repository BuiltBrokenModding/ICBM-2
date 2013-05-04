package icbm.wanyi;

import icbm.api.ICBM;
import icbm.core.ICBMTab;
import icbm.core.ZhuYaoBase;
import icbm.wanyi.b.BBuoLi;
import icbm.wanyi.b.BBuoLiPan;
import icbm.wanyi.b.BEnNiu;
import icbm.wanyi.b.BNiTu;
import icbm.wanyi.b.BYinGanQi;
import icbm.wanyi.b.BYinXing;
import icbm.wanyi.b.BZha;
import icbm.wanyi.b.IBNiTu;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.core.item.ItemElectric;
import calclavia.lib.UniversalRecipes;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ZhuYaoWanYi.NAME, name = ZhuYaoWanYi.NAME, version = ICBM.VERSION, dependencies = "after:BasicComponents;after:AtomicScience", useMetadata = true)
@NetworkMod(channels = ZhuYaoWanYi.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = WanYiPacketGuanLi.class)
public class ZhuYaoWanYi extends ZhuYaoBase
{
	public static final String NAME = ICBM.NAME + "|Contraption";
	public static final String CHANNEL = ICBM.NAME + "|C";

	@Instance(NAME)
	public static ZhuYaoWanYi instance;

	@Metadata(NAME)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "icbm.wanyi.ClientProxy", serverSide = "icbm.wanyi.CommonProxy")
	public static CommonProxy proxy;

	// Blocks
	public static Block bBuoLiPan, bBuoLiEnNiu, bYinGanQi, bZha, bYinXing, bNiTu, bBuoLi;

	// Items
	public static Item itYao;
	public static ItemElectric itHuoLaunQi;
	public static ItemElectric itGenZongQi;

	@Override
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		NetworkRegistry.instance().registerGuiHandler(this, ZhuYaoWanYi.proxy);

		ZhuYaoBase.CONFIGURATION.load();

		// Blocks
		bBuoLiPan = new BBuoLiPan(ZhuYaoBase.CONFIGURATION.getBlock("Glass Pressure Plate", ICBM.BLOCK_ID_PREFIX - 1).getInt());
		bBuoLiEnNiu = new BEnNiu(ZhuYaoBase.CONFIGURATION.getBlock("Glass Button", ICBM.BLOCK_ID_PREFIX - 2).getInt());
		bYinGanQi = new BYinGanQi(ICBM.BLOCK_ID_PREFIX - 3);
		bZha = new BZha(ICBM.BLOCK_ID_PREFIX - 4);
		bYinXing = new BYinXing(ICBM.BLOCK_ID_PREFIX - 5);
		bNiTu = new BNiTu(ICBM.BLOCK_ID_PREFIX - 6);
		bBuoLi = new BBuoLi(ICBM.BLOCK_ID_PREFIX - 7);

		// ITEMS
		itYao = new ItYao(ZhuYaoBase.CONFIGURATION.getItem("ItemID3", ICBM.ITEM_ID_PREFIX + 2).getInt());
		itHuoLaunQi = new ItHuoLuanQi(ZhuYaoBase.CONFIGURATION.getItem("ItemID10", ICBM.ITEM_ID_PREFIX + 9).getInt());
		itGenZongQi = new ItGenZongQi(ZhuYaoBase.CONFIGURATION.getItem("ItemID11", ICBM.ITEM_ID_PREFIX + 10).getInt());

		ZhuYaoBase.CONFIGURATION.save();

		ICBMTab.itemStack = new ItemStack(bYinGanQi);

		// -- Registering Blocks
		GameRegistry.registerBlock(bBuoLiPan, "bBuoLiPan");
		GameRegistry.registerBlock(bBuoLiEnNiu, "bBuoLiEnNiu");
		GameRegistry.registerBlock(bYinGanQi, "bYinGanQi");
		GameRegistry.registerBlock(bYinXing, "bYinXing");
		GameRegistry.registerBlock(bBuoLi, "bBuoLi");
		GameRegistry.registerBlock(bZha, IBZha.class, "bZha");
		GameRegistry.registerBlock(bNiTu, IBNiTu.class, "bNiTu");

		ZhuYaoWanYi.proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		ZhuYaoBase.setModMetadata(NAME, metadata);
	}

	@Override
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

		/**
		 * Add all Recipes
		 */
		// Spikes
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 6), new Object[] { "CCC", "BBB", 'C', Block.cactus, 'B', UniversalRecipes.SECONDARY_METAL }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 6), new Object[] { "CCC", "BBB", 'C', Block.cactus, 'B', Item.ingotIron }));
		GameRegistry.addRecipe(new ItemStack(bZha, 1, 1), new Object[] { "E", "S", 'E', ZhuYaoBase.itDu, 'S', bZha });
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 1, 2), new Object[] { "E", "S", 'E', "dustSulfur", 'S', bZha }));

		// Camouflage
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bYinXing, 12), new Object[] { "WGW", "GCG", "WGW", 'C', UniversalRecipes.CIRCUIT_T1, 'G', Block.glass, 'W', new ItemStack(Block.cloth, 1, OreDictionary.WILDCARD_VALUE) }));

		if (OreDictionary.getOres(UniversalRecipes.BATTERY).size() > 0)
		{
			// Tracker
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itGenZongQi), new Object[] { " Z ", "SBS", "SCS", 'Z', Item.compass, 'C', UniversalRecipes.CIRCUIT_T1, 'B', ElectricItemHelper.getUncharged(OreDictionary.getOres(UniversalRecipes.BATTERY).get(0)), 'S', UniversalRecipes.PRIMARY_METAL }));
		}
		// Glass Pressure Plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoWanYi.bBuoLiPan, 1, 0), new Object[] { "##", '#', Block.glass }));

		// Glass Button
		GameRegistry.addRecipe(new ItemStack(bBuoLiEnNiu, 2), new Object[] { "G", "G", 'G', Block.glass });

		// Proximity Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(bYinGanQi, new Object[] { "SSS", "S?S", "SSS", 'S', UniversalRecipes.PRIMARY_METAL, '?', ElectricItemHelper.getUncharged(itGenZongQi) }));

		// Signal Disrupter
		GameRegistry.addRecipe(new ShapedOreRecipe(itHuoLaunQi, new Object[] { "WWW", "SCS", "SSS", 'S', UniversalRecipes.PRIMARY_METAL, 'C', UniversalRecipes.CIRCUIT_T1, 'W', UniversalRecipes.WIRE }));

		// Antidote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itYao, 6), new Object[] { "@@@", "@@@", "@@@", '@', Item.pumpkinSeeds }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itYao), new Object[] { "@@@", "@@@", "@@@", '@', Item.seeds }));

		// Concrete
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bNiTu, 8, 0), new Object[] { "SGS", "GWG", "SGS", 'G', Block.gravel, 'S', Block.sandStone, 'W', Item.bucketWater }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bNiTu, 8, 1), new Object[] { "COC", "OCO", "COC", 'C', new ItemStack(bNiTu, 1, 0), 'O', Block.obsidian }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bNiTu, 8, 2), new Object[] { "COC", "OCO", "COC", 'C', new ItemStack(bNiTu, 1, 1), 'O', UniversalRecipes.PRIMARY_METAL }));

		// Reinforced Glass
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bBuoLi, 8), new Object[] { "IGI", "GIG", "IGI", 'G', Block.glass, 'I', Item.ingotIron }));

		ZhuYaoWanYi.proxy.init();
	}
}
