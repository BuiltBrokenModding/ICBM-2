package icbm;

import icbm.api.ICBM;
import icbm.cart.EChe;
import icbm.daodan.DaoDan;
import icbm.daodan.EDaoDan;
import icbm.daodan.ItDaoDan;
import icbm.daodan.ItTeBieDaoDan;
import icbm.dianqi.ItFaSheQi;
import icbm.dianqi.ItGenZongQi;
import icbm.dianqi.ItHuoLuanQi;
import icbm.dianqi.ItJieJa;
import icbm.dianqi.ItLeiDaQiang;
import icbm.dianqi.ItLeiShiZhiBiao;
import icbm.dianqi.ItYaoKong;
import icbm.jiqi.BJiQi;
import icbm.jiqi.BYinGanQi;
import icbm.jiqi.EFake;
import icbm.jiqi.IBJiQi;
import icbm.po.PChuanRanDu;
import icbm.po.PDaDu;
import icbm.po.PDongShang;
import icbm.zhapin.BZhaDan;
import icbm.zhapin.EShouLiuDan;
import icbm.zhapin.EZhaDan;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.IBZhaDan;
import icbm.zhapin.ItShouLiuDan;
import icbm.zhapin.ZhaPin;

import java.io.File;
import java.util.List;

import net.minecraft.src.BehaviorDefaultDispenseItem;
import net.minecraft.src.Block;
import net.minecraft.src.BlockDispenser;
import net.minecraft.src.BlockRail;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumFacing;
import net.minecraft.src.IBehaviorDispenseItem;
import net.minecraft.src.IBlockSource;
import net.minecraft.src.ICommandManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.UpdateNotifier;
import universalelectricity.prefab.multiblock.BlockMulti;
import universalelectricity.prefab.ore.OreGenBase;
import universalelectricity.prefab.ore.OreGenerator;
import atomicscience.api.BlockRadioactive;
import atomicscience.api.PoisonRadiation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = ZhuYao.MING_ZI, name = ZhuYao.MING_ZI, version = ZhuYao.BAN_BEN, dependencies = "after:BasicComponents;after:AtomicScience")
@NetworkMod(channels = ZhuYao.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = ICBMPacketManager.class)
public class ZhuYao
{
	@Instance("ICBM")
	public static ZhuYao instance;

	/**
	 * The version of ICBM.
	 */
	public static final String BAN_BEN = "0.6.4";

	public static final String MING_ZI = "ICBM";

	public static final String CHANNEL = MING_ZI;

	public static final ICBMTab TAB = new ICBMTab();

	// Configurations
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/ICBM.cfg"));

	private static final String[] YU_YAN = new String[]
	{ "en_US" };

	@SidedProxy(clientSide = "icbm.ICBMClient", serverSide = "icbm.ICBMCommon")
	public static ICBMCommon proxy;

	public static final int ENTITY_ID_PREFIX = 50;

	/**
	 * Settings and Configurations
	 */
	public static boolean GAO_PARTICLE;
	public static boolean GAO_VISUAL;
	public static boolean ZAI_KUAI;
	public static int DAO_DAN_ZUI_YUAN;

	// BLOCKS
	public static final int B_HAO_MA = 3880;
	public static Block bLiu;
	public static Block bBuoLiPan;
	public static Block bZhaDan;
	public static Block bJiQi;
	public static Block bFuShe;
	public static Block bYinGanQi;
	public static BlockMulti bJia;
	public static BEnNiu bBuoLiEnNiu;
	public static Block bZha;
	public static Block bYinXing;

	// ITEMS
	public static final int I_HAO_MA = 3900;
	public static Item itLiu;
	public static Item itDu;
	public static Item itYao;
	public static Item itDaoDan;
	public static Item itTeBieDaoDan;

	public static ItemElectric itJieJa;
	public static ItemElectric itLeiDaQiang;
	public static ItemElectric itYaoKong;
	public static ItemElectric itLeiSheZhiBiao;
	public static ItemElectric itHuoLaunQi;
	public static ItemElectric itGenZongQi;
	public static ItemElectric itFaSheQi;

	public static Item itShouLiuDan;
	public static Item itZiDan;

	public static Item itChe;

	public static final Du DU_DU = new Du("Chemical", 1, false);
	public static final Du DU_CHUAN_RAN = new Du("Contagious", 1, true);

	public static OreGenBase liuGenData;

	/**
	 * Some texture file directory references.
	 */
	public static final String TEXTURE_FILE_PATH = "/icbm/textures/";
	public static final String BLOCK_TEXTURE_FILE = TEXTURE_FILE_PATH + "blocks.png";
	public static final String ITEM_TEXTURE_FILE = TEXTURE_FILE_PATH + "items.png";
	public static final String TRACKER_TEXTURE_FILE = TEXTURE_FILE_PATH + "tracker.png";
	public static final String YU_YAN_PATH = "/icbm/yuyan/";

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		UniversalElectricity.register(this, 1, 2, 0, false);

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

		/**
		 * Load all languages.
		 */
		for (String language : YU_YAN)
		{
			LanguageRegistry.instance().loadLocalization(ZhuYao.YU_YAN_PATH + language + ".properties", language, false);
		}

		CONFIGURATION.load();
		GAO_PARTICLE = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Spawn Particle Effects", true).getBoolean(true);
		GAO_VISUAL = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Advanced Visual Effects", false).getBoolean(false);
		ZAI_KUAI = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", true).getBoolean(true);
		DAO_DAN_ZUI_YUAN = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", 2000).getInt(2000);
		BaoHu.SHE_DING_BAO_HU = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Protect Worlds by Default", false).getBoolean(false);

		// BLOCKS
		bLiu = new BLiu(CONFIGURATION.getBlock("BlockID1", B_HAO_MA - 1).getInt());
		bBuoLiPan = new BBuoLiPan(CONFIGURATION.getBlock("BlockID2", B_HAO_MA + 0).getInt(), 0);
		bZhaDan = new BZhaDan(CONFIGURATION.getBlock("BlockID3", B_HAO_MA + 1).getInt(), 16);
		bJiQi = new BJiQi(CONFIGURATION.getBlock("BlockID4", B_HAO_MA + 3).getInt());
		bYinGanQi = new BYinGanQi(CONFIGURATION.getBlock("BlockID5", B_HAO_MA + 6).getInt(), 7);
		bJia = new BlockMulti(CONFIGURATION.getBlock("BlockID6", B_HAO_MA + 7).getInt());
		bBuoLiEnNiu = new BEnNiu(CONFIGURATION.getBlock("BlockID7", B_HAO_MA + 8).getInt());
		bZha = new BZha(CONFIGURATION.getBlock("BlockID8", B_HAO_MA + 9).getInt(), 1);
		bYinXing = new BYinXing(CONFIGURATION.getBlock("BlockID9", B_HAO_MA + 10).getInt());

		// ITEMS
		itLiu = new ICBMItem("sulfur", CONFIGURATION.getItem("ItemID1", I_HAO_MA + 0).getInt(), 3, CreativeTabs.tabMaterials);
		itDu = new ICBMItem("poisonPowder", CONFIGURATION.getItem("ItemID2", I_HAO_MA + 1).getInt(), 0, CreativeTabs.tabMaterials);
		itYao = new ItYao(CONFIGURATION.getItem("ItemID3", I_HAO_MA + 2).getInt(), 5);
		itDaoDan = new ItDaoDan("missile", CONFIGURATION.getItem("ItemID4", I_HAO_MA + 3).getInt(), 32);
		itTeBieDaoDan = new ItTeBieDaoDan("specialMissile", CONFIGURATION.getItem("ItemID5", I_HAO_MA + 4).getInt(), 32);

		itJieJa = new ItJieJa(CONFIGURATION.getItem("ItemID6", I_HAO_MA + 5).getInt(), 21);
		itLeiDaQiang = new ItLeiDaQiang(CONFIGURATION.getItem("ItemID7", I_HAO_MA + 6).getInt(), 19);
		itYaoKong = new ItYaoKong(CONFIGURATION.getItem("ItemID8", I_HAO_MA + 7).getInt(), 20);
		itLeiSheZhiBiao = new ItLeiShiZhiBiao(CONFIGURATION.getItem("ItemID9", I_HAO_MA + 8).getInt(), 22);
		itHuoLaunQi = new ItHuoLuanQi(CONFIGURATION.getItem("ItemID10", I_HAO_MA + 9).getInt(), 23);
		itGenZongQi = new ItGenZongQi(CONFIGURATION.getItem("ItemID11", I_HAO_MA + 10).getInt(), 0);
		itFaSheQi = new ItFaSheQi(CONFIGURATION.getItem("ItemID12", I_HAO_MA + 11).getInt(), 0);

		itShouLiuDan = new ItShouLiuDan(CONFIGURATION.getItem("ItemID13", I_HAO_MA + 12).getInt(), 64);
		itZiDan = new ItZiDan("bullet", CONFIGURATION.getItem("ItemID14", I_HAO_MA + 13).getInt(), 80);

		itChe = new ItChe(CONFIGURATION.getItem("ItemID15", I_HAO_MA + 11).getInt(), 135);

		liuGenData = new GenLiu("Sulfur Ore", "oreSulfur", new ItemStack(bLiu), 0, 40, 25, 15).enable(CONFIGURATION);
		CONFIGURATION.save();

		BlockDispenser.dispenseBehaviorRegistry.putObject(ZhuYao.itShouLiuDan, new IBehaviorDispenseItem()
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
					EnumFacing enumFacing = EnumFacing.func_82600_a(blockSource.func_82620_h());

					EShouLiuDan entity = new EShouLiuDan(world, new Vector3(x, y, z), itemStack.getItemDamage());
					entity.setThrowableHeading((double) enumFacing.func_82601_c(), 0.10000000149011612D, (double) enumFacing.func_82599_e(), 0.5F, 1.0F);
					world.spawnEntityInWorld(entity);
				}

				itemStack.stackSize--;
				return itemStack;
			}
		});

		BlockDispenser.dispenseBehaviorRegistry.putObject(ZhuYao.itChe, new IBehaviorDispenseItem()
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

					EnumFacing var3 = EnumFacing.func_82600_a(blockSource.func_82620_h());
					World var4 = blockSource.getWorld();
					double var5 = blockSource.getX() + (double) ((float) var3.func_82601_c() * 1.125F);
					double var7 = blockSource.getY();
					double var9 = blockSource.getZ() + (double) ((float) var3.func_82599_e() * 1.125F);
					int var11 = blockSource.getXInt() + var3.func_82601_c();
					int var12 = blockSource.getYInt();
					int var13 = blockSource.getZInt() + var3.func_82599_e();
					int var14 = var4.getBlockId(var11, var12, var13);
					double var15;

					if (BlockRail.isRailBlock(var14))
					{
						var15 = 0.0D;
					}
					else
					{
						if (var14 != 0 || !BlockRail.isRailBlock(var4.getBlockId(var11, var12 - 1, var13))) { return this.defaultItemDispenseBehavior.dispense(blockSource, itemStack); }

						var15 = -1.0D;
					}

					EChe var22 = new EChe(world, var5, var7 + var15, var9, itemStack.getItemDamage());
					world.spawnEntityInWorld(var22);
					world.playAuxSFX(1000, x, y, z, 0);
				}

				itemStack.stackSize--;
				return itemStack;
			}
		});

		// -- Registering Blocks
		GameRegistry.registerBlock(bLiu);
		GameRegistry.registerBlock(bBuoLiPan);
		GameRegistry.registerBlock(bZhaDan, IBZhaDan.class);
		GameRegistry.registerBlock(bJiQi, IBJiQi.class);
		GameRegistry.registerBlock(bYinGanQi);
		GameRegistry.registerBlock(bJia);
		GameRegistry.registerBlock(bBuoLiEnNiu);
		GameRegistry.registerBlock(bYinXing);
		GameRegistry.registerBlock(bZha, IBZha.class);

		if (OreDictionary.getOres("blockRadioactive").size() > 0)
		{
			bFuShe = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
			System.out.println("Detected radioative block from another mod.");
		}

		OreDictionary.registerOre("dustSulfur", itLiu);

		OreGenerator.addOre(liuGenData);
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new DaoDanCLCallBack());
		MinecraftForge.EVENT_BUS.register(this);

		// Set ICBM API Variables
		ICBM.explosiveBlock = this.bZhaDan;
		ICBM.explosionManager = ZhaPin.class;

		UpdateNotifier.INSTANCE.checkUpdate(MING_ZI, BAN_BEN, "http://calclavia.com/downloads/icbm/modversion.txt");

		this.proxy.preInit();
	}

	public class DaoDanCLCallBack implements ForgeChunkManager.LoadingCallback
	{
		@Override
		public void ticketsLoaded(List<Ticket> tickets, World world)
		{
			for (Ticket ticket : tickets)
			{
				if (ticket.getEntity() != null)
				{
					((EDaoDan) ticket.getEntity()).daoDanInit(ticket);
				}
			}
		}
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		if (bFuShe == null)
		{
			CONFIGURATION.load();
			bFuShe = new BlockRadioactive(CONFIGURATION.getBlock("Radioactive Block", B_HAO_MA + 5).getInt(), 4, ZhuYao.BLOCK_TEXTURE_FILE);
			CONFIGURATION.save();
			GameRegistry.registerBlock(bFuShe);
		}

		for (int i = 0; i < ZhaPin.list.length; i++)
		{
			if (ZhaPin.list[i] != null)
			{
				ZhaPin.list[i].init();
			}
		}

		for (int i = 0; i < ((ItTeBieDaoDan) ZhuYao.itTeBieDaoDan).names.length; i++)
		{
			LanguageRegistry.addName(new ItemStack(ZhuYao.itTeBieDaoDan, 1, i), ((ItTeBieDaoDan) ZhuYao.itTeBieDaoDan).names[i]);
		}

		LanguageRegistry.addName(new ItemStack(ZhuYao.bZhaDan, 1, ZhaPin.diLei.getID()), LanguageRegistry.instance().getStringLocalization("icbm.sMine"));

		// Explosives and missile recipe
		for (int i = 0; i < ZhaPin.E_SI_ID; i++)
		{
			LanguageRegistry.addName(new ItemStack(ZhuYao.bZhaDan, 1, i), ZhaPin.list[i].getZhaPinMing());
			LanguageRegistry.addName(new ItemStack(ZhuYao.itDaoDan, 1, i), ZhaPin.list[i].getDaoDanMing());

			if (i < ZhaPin.E_YI_ID)
			{
				LanguageRegistry.addName(new ItemStack(itShouLiuDan, 1, i), ZhaPin.list[i].getShouLiuDanMing());
			}

			if (i < ZhaPin.E_ER_ID)
			{
				LanguageRegistry.addName(new ItemStack(itChe, 1, i), ZhaPin.list[i].getCheMing());
			}
		}

		// -- Recipes
		// Spikes
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 5), new Object[]
		{ "CCC", "BBB", 'C', Block.cactus, 'B', "ingotBronze" }));

		GameRegistry.addRecipe(new ItemStack(bZha, 1, 1), new Object[]
		{ "E", "S", 'E', itDu, 'S', bZha });

		GameRegistry.addRecipe(new ItemStack(bZha, 1, 2), new Object[]
		{ "E", "S", 'E', itLiu, 'S', bZha });

		// Camouflage
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bYinXing, 1, 5), new Object[]
		{ "WGW", "GCG", "WGW", 'C', "basicCircuit", 'G', Block.glass, 'W', Block.cloth }));

		// Glass Button
		GameRegistry.addRecipe(new ItemStack(bBuoLiEnNiu, 2), new Object[]
		{ "G", "G", 'G', Block.glass });

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itZiDan, 3, 0), new Object[]
		{ "@", "!", "!", '@', Item.diamond, '!', "ingotBronze" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itZiDan, 1, 1), new Object[]
		{ "@", "!", "!", '@', new ItemStack(ZhuYao.bZhaDan, 1, ZhaPin.fanWuSu.getID()), '!', ZhuYao.itZiDan }));

		// Poison Powder
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itDu), new Object[]
		{ Item.fermentedSpiderEye, Item.rottenFlesh }));

		// Sulfur
		GameRegistry.addSmelting(bLiu.blockID, new ItemStack(itLiu), 0.8f);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 5), new Object[]
		{ "@@@", "@?@", "@@@", '@', "dustSulfur", '?', Item.coal }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 5), new Object[]
		{ "@@@", "@?@", "@@@", '@', "dustSulfur", '?', new ItemStack(Item.coal, 1, 1) }));

		// Radar Gun
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itLeiDaQiang), new Object[]
		{ "@#!", " $!", "  !", '@', Block.glass, '!', "ingotSteel", '#', "basicCircuit", '$', Block.stoneButton }));
		// Remote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itYaoKong), new Object[]
		{ "?@@", "@#$", "@@@", '@', "ingotSteel", '?', Item.redstone, '#', "advancedCircuit", '$', Block.stoneButton }));
		// Laser Designator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itLeiSheZhiBiao), new Object[]
		{ "!  ", " ? ", "  @", '@', ZhuYao.itYaoKong.getUncharged(), '?', "eliteCircuit", '!', ZhuYao.itLeiDaQiang.getUncharged() }));
		// Proximity Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bYinGanQi), new Object[]
		{ " ! ", "!?!", " ! ", '!', "plateSteel", '?', "eliteCircuit" }));
		// Signal Disrupter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itHuoLaunQi), new Object[]
		{ "!", "?", '!', ZhuYao.itYaoKong.getUncharged(), '?', ZhuYao.bYinGanQi }));
		// Antidote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itYao, 2), new Object[]
		{ "@@@", "@@@", "@@@", '@', Item.pumpkinSeeds }));
		// Defuser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itJieJa), new Object[]
		{ "?  ", " @ ", "  !", '@', "advancedCircuit", '!', "plateSteel", '?', "copperWire" }));
		// Missile Launcher Platform
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 0), new Object[]
		{ "! !", "!@!", "!!!", '!', "ingotBronze", '@', "plateSteel" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 1), new Object[]
		{ "! !", "! !", "!@!", '@', new ItemStack(ZhuYao.bJiQi, 1, 0), '!', "ingotSteel" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 2), new Object[]
		{ "! !", "! !", "!@!", '@', new ItemStack(ZhuYao.bJiQi, 1, 1), '!', "plateSteel" }));
		// Missile Launcher Computer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 3), new Object[]
		{ "!!!", "!#!", "!?!", '#', "basicCircuit", '!', Block.glass, '?', "copperWire" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 4), new Object[]
		{ "!$!", "!#!", "!?!", '#', "advancedCircuit", '!', "ingotSteel", '?', "copperWire", '$', new ItemStack(ZhuYao.bJiQi, 1, 3) }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 5), new Object[]
		{ "!$!", "!#!", "!?!", '#', "eliteCircuit", '!', Item.ingotGold, '?', "copperWire", '$', new ItemStack(ZhuYao.bJiQi, 1, 4) }));
		// Missile Launcher Frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 6), new Object[]
		{ "! !", "!!!", "! !", '!', "ingotBronze" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 7), new Object[]
		{ "! !", "!@!", "! !", '!', "ingotSteel", '@', new ItemStack(ZhuYao.bJiQi, 1, 6) }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 8), new Object[]
		{ "! !", "!@!", "! !", '!', "plateSteel", '@', new ItemStack(ZhuYao.bJiQi, 1, 7) }));
		// Radar Station
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 9), new Object[]
		{ "?@?", " ! ", "!#!", '@', ZhuYao.itLeiDaQiang.getUncharged(), '!', "ingotSteel", '#', "eliteCircuit", '?', Item.ingotGold }));
		// EMP Tower
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 10), new Object[]
		{ "???", "@!@", "?#?", '?', "plateSteel", '!', "eliteCircuit", '@', "batteryBox", '#', "motor" }));
		// Railgun
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 11), new Object[]
		{ "?!#", "@@@", '@', "plateSteel", '!', ZhuYao.itLeiDaQiang.getUncharged(), '#', Item.diamond, '?', "eliteCircuit" }));
		// Cruise Launcher
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 12), new Object[]
		{ "?! ", "@@@", '@', "plateSteel", '!', new ItemStack(ZhuYao.bJiQi, 1, 2), '?', new ItemStack(ZhuYao.bJiQi, 1, 8) }));
		// Laser Turret
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bJiQi, 1, 13), new Object[]
		{ "?!#", "@@@", '@', "plateSteel", '!', ZhuYao.itLeiDaQiang.getUncharged(), '#', Item.diamond, '?', "advancedCircuit" }));
		// Glass Pressure Plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.bBuoLiPan, 1, 0), new Object[]
		{ "##", '#', Block.glass }));
		// Missiles
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itTeBieDaoDan, 1, 0), new Object[]
		{ " @ ", "@#@", "@?@", '@', "ingotSteel", '?', "oilBucket", '#', "basicCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itTeBieDaoDan, 1, 1), new Object[]
		{ "!", "?", "@", '@', new ItemStack(ZhuYao.itTeBieDaoDan, 1, 0), '?', new ItemStack(ZhuYao.bZhaDan, 1, 0), '!', "basicCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itTeBieDaoDan, 1, 2), new Object[]
		{ " ! ", " ? ", "!@!", '@', new ItemStack(ZhuYao.itTeBieDaoDan, 1, 0), '?', DaoDan.list[ZhaPin.qunDan.getID()].getItemStack(), '!', new ItemStack(ZhuYao.itDaoDan, 1, 0) }));

		/**
		 * Add all explosive recipes.
		 */
		for (int i = 0; i < ZhaPin.E_SI_ID; i++)
		{
			// Missile
			RecipeHelper.addRecipe(new ShapelessOreRecipe(new ItemStack(ZhuYao.itDaoDan, 1, i), new Object[]
			{ new ItemStack(ZhuYao.itTeBieDaoDan, 1, 0), new ItemStack(ZhuYao.bZhaDan, 1, i) }), ZhaPin.list[i].getDaoDanMing(), CONFIGURATION, true);

			if (i < ZhaPin.E_YI_ID)
			{
				// Grenade
				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itShouLiuDan, 1, i), new Object[]
				{ "?", "@", '@', new ItemStack(ZhuYao.bZhaDan, 1, i), '?', Item.silk }), CONFIGURATION, true);
			}

			if (i < ZhaPin.E_ER_ID)
			{
				// Minecart
				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYao.itChe, 1, i), new Object[]
				{ "?", "@", '?', new ItemStack(ZhuYao.bZhaDan, 1, i), '@', Item.minecartEmpty }), CONFIGURATION, true);
			}
		}

		EntityRegistry.registerGlobalEntityID(EZhaDan.class, "ICBMExplosive", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EDaoDan.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EZhaPin.class, "ICBMProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EFeiBlock.class, "ICBMGravityBlock", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EGuang.class, "ICBMLightBeam", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(ESuiPian.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EShouLiuDan.class, "ICBMGrenade", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EFake.class, "ICBMFake", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EChe.class, "ICBMChe", EntityRegistry.findGlobalUniqueEntityId());

		EntityRegistry.registerModEntity(EZhaDan.class, "ICBMExplosive", ENTITY_ID_PREFIX, this, 50, 5, true);
		EntityRegistry.registerModEntity(EDaoDan.class, "ICBMMissile", ENTITY_ID_PREFIX + 1, this, 100, 1, true);
		EntityRegistry.registerModEntity(EZhaPin.class, "ICBMProceduralExplosion", ENTITY_ID_PREFIX + 2, this, 100, 5, true);
		EntityRegistry.registerModEntity(EFeiBlock.class, "ICBMGravityBlock", ENTITY_ID_PREFIX + 3, this, 50, 15, true);
		EntityRegistry.registerModEntity(EGuang.class, "ICBMLightBeam", ENTITY_ID_PREFIX + 4, this, 80, 5, true);
		EntityRegistry.registerModEntity(ESuiPian.class, "ICBMFragment", ENTITY_ID_PREFIX + 5, this, 40, 8, true);
		EntityRegistry.registerModEntity(EShouLiuDan.class, "ICBMGrenade", ENTITY_ID_PREFIX + 6, this, 50, 5, true);
		EntityRegistry.registerModEntity(EFake.class, "ICBMFake", ENTITY_ID_PREFIX + 7, this, 50, 5, true);
		EntityRegistry.registerModEntity(EChe.class, "ICBMChe", ENTITY_ID_PREFIX + 8, this, 50, 4, true);

		// Register potion effects
		PoisonRadiation.register();
		PDaDu.INSTANCE.register();
		PChuanRanDu.INSTANCE.register();
		PDongShang.INSTANCE.register();

		this.proxy.init();
	}

	public static Vector3 getLook(float rotationYaw, float rotationPitch)
	{
		float var2;
		float var3;
		float var4;
		float var5;

		var2 = MathHelper.cos(-rotationYaw * 0.017453292F - (float) Math.PI);
		var3 = MathHelper.sin(-rotationYaw * 0.017453292F - (float) Math.PI);
		var4 = -MathHelper.cos(-rotationPitch * 0.017453292F);
		var5 = MathHelper.sin(-rotationPitch * 0.017453292F);
		return new Vector3(var3 * var4, var5, var2 * var4);
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
		serverCommandManager.registerCommand(new MingLing());

		BaoHu.nbtData = BaoHu.loadData("ICBM");
	}

	@ServerStopping
	public void serverStopping(FMLServerStoppingEvent event)
	{
		BaoHu.saveData(BaoHu.nbtData, "ICBM");
	}
}
