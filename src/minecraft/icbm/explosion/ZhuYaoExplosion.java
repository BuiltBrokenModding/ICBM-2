package icbm.explosion;

import icbm.api.ICBM;
import icbm.api.ICBMTab;
import icbm.contraption.BBuoLiPan;
import icbm.contraption.BEnNiu;
import icbm.contraption.BYinXing;
import icbm.contraption.BZha;
import icbm.contraption.IBZha;
import icbm.contraption.ItYao;
import icbm.core.BLiu;
import icbm.core.BaoHu;
import icbm.core.GenLiu;
import icbm.core.ICBMPacketManager;
import icbm.core.MingLing;
import icbm.explosion.cart.EChe;
import icbm.explosion.cart.ItChe;
import icbm.explosion.daodan.DaoDan;
import icbm.explosion.daodan.EDaoDan;
import icbm.explosion.daodan.ItDaoDan;
import icbm.explosion.daodan.ItTeBieDaoDan;
import icbm.explosion.dianqi.ItFaSheQi;
import icbm.explosion.dianqi.ItGenZongQi;
import icbm.explosion.dianqi.ItHuoLuanQi;
import icbm.explosion.dianqi.ItJieJa;
import icbm.explosion.dianqi.ItLeiDaQiang;
import icbm.explosion.dianqi.ItLeiSheZhiBiao;
import icbm.explosion.dianqi.ItYaoKong;
import icbm.explosion.jiqi.BJiQi;
import icbm.explosion.jiqi.BJiQi.JiQi;
import icbm.explosion.jiqi.BYinGanQi;
import icbm.explosion.jiqi.EFake;
import icbm.explosion.jiqi.IBJiQi;
import icbm.explosion.po.PChuanRanDu;
import icbm.explosion.po.PDaDu;
import icbm.explosion.po.PDongShang;
import icbm.explosion.zhapin.BZhaDan;
import icbm.explosion.zhapin.EShouLiuDan;
import icbm.explosion.zhapin.EZhaDan;
import icbm.explosion.zhapin.EZhaPin;
import icbm.explosion.zhapin.IBZhaDan;
import icbm.explosion.zhapin.ItShouLiuDan;
import icbm.explosion.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRail;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.UpdateNotifier;
import universalelectricity.prefab.multiblock.BlockMulti;
import universalelectricity.prefab.ore.OreGenBase;
import universalelectricity.prefab.ore.OreGenerator;
import atomicscience.api.BlockRadioactive;
import atomicscience.api.PoisonRadiation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ICBM.NAME, name = ICBM.NAME, version = ICBM.VERSION, dependencies = "after:BasicComponents;after:AtomicScience")
@NetworkMod(channels = ZhuYaoExplosion.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = ICBMPacketManager.class)
public class ZhuYaoExplosion
{
	@Instance("ICBM")
	public static ZhuYaoExplosion instance;

	public static final String CHANNEL = ICBM.NAME;

	private static final String[] YU_YAN = new String[] { "en_US", "zh_CN", "es_ES" };

	@SidedProxy(clientSide = "icbm.explosion.ClientProxy", serverSide = "icbm.explosion.CommonProxy")
	public static CommonProxy proxy;

	public static final int ENTITY_ID_PREFIX = 50;

	/**
	 * Settings and Configurations
	 */
	public static boolean ZAI_KUAI;
	public static int DAO_DAN_ZUI_YUAN;

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

	public static final String SMINE_TEXTURE = ICBM.TEXTURE_FILE_PATH + "S-Mine.png";
	public static final String BLOCK_TEXTURE_FILE = ICBM.TEXTURE_FILE_PATH + "blocks.png";
	public static final String ITEM_TEXTURE_FILE = ICBM.TEXTURE_FILE_PATH + "items.png";
	public static final String TRACKER_TEXTURE_FILE = ICBM.TEXTURE_FILE_PATH + "tracker.png";
	public static final String YU_YAN_PATH = "/icbm/yuyan/";

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		UniversalElectricity.register(this, 1, 2, 5, false);

		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

		System.out.println(ICBM.NAME + " Loaded " + TranslationHelper.loadLanguages(YU_YAN_PATH, YU_YAN) + " languages.");

		ICBM.CONFIGURATION.load();
		ZAI_KUAI = ICBM.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", true).getBoolean(true);
		DAO_DAN_ZUI_YUAN = ICBM.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", 10000).getInt(10000);
		BaoHu.SHE_DING_BAO_HU = ICBM.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Protect Worlds by Default", false).getBoolean(false);

		// BLOCKS
		bLiu = new BLiu(ICBM.CONFIGURATION.getBlock("BlockID1", ICBM.BLOCK_ID_PREFIX + 0).getInt());

		bBuoLiPan = new BBuoLiPan(ICBM.CONFIGURATION.getBlock("BlockID2", ICBM.BLOCK_ID_PREFIX + 1).getInt());
		bBuoLiEnNiu = new BEnNiu(ICBM.CONFIGURATION.getBlock("BlockID7", ICBM.BLOCK_ID_PREFIX + 2).getInt());

		bZhaDan = new BZhaDan(ICBM.CONFIGURATION.getBlock("BlockID3", ICBM.BLOCK_ID_PREFIX + 3).getInt(), 16);
		bJiQi = new BJiQi(ICBM.CONFIGURATION.getBlock("BlockID4", ICBM.BLOCK_ID_PREFIX + 4).getInt());
		bYinGanQi = new BYinGanQi(ICBM.CONFIGURATION.getBlock("BlockID5", ICBM.BLOCK_ID_PREFIX + 5).getInt(), 7);
		bJia = new BlockMulti(ICBM.CONFIGURATION.getBlock("BlockID6", ICBM.BLOCK_ID_PREFIX + 6).getInt());
		bZha = new BZha(ICBM.CONFIGURATION.getBlock("BlockID8", ICBM.BLOCK_ID_PREFIX + 7).getInt(), 1);
		bYinXing = new BYinXing(ICBM.CONFIGURATION.getBlock("BlockID9", ICBM.BLOCK_ID_PREFIX + 8).getInt(), 11);

		// ITEMS
		itDu = new Item(ICBM.CONFIGURATION.getItem("ItemID2", ICBM.ITEM_ID_PREFIX + 0).getInt()).setCreativeTab(ICBMTab.INSTANCE).setTextureFile(ITEM_TEXTURE_FILE).setItemName("poisonPowder").setIconIndex(0);
		itLiu = new Item(ICBM.CONFIGURATION.getItem("ItemID1", ICBM.ITEM_ID_PREFIX + 1).getInt()).setCreativeTab(ICBMTab.INSTANCE).setTextureFile(ITEM_TEXTURE_FILE).setItemName("sulfur").setIconIndex(1);
		itYao = new ItYao(ICBM.CONFIGURATION.getItem("ItemID3", ICBM.ITEM_ID_PREFIX + 2).getInt(), 2);
		itDaoDan = new ItDaoDan(ICBM.CONFIGURATION.getItem("ItemID4", ICBM.ITEM_ID_PREFIX + 3).getInt(), 32);
		itTeBieDaoDan = new ItTeBieDaoDan(ICBM.CONFIGURATION.getItem("ItemID5", ICBM.ITEM_ID_PREFIX + 4).getInt(), 32);

		itJieJa = new ItJieJa(ICBM.CONFIGURATION.getItem("ItemID6", ICBM.ITEM_ID_PREFIX + 5).getInt(), 5);
		itLeiDaQiang = new ItLeiDaQiang(ICBM.CONFIGURATION.getItem("ItemID7", ICBM.ITEM_ID_PREFIX + 6).getInt(), 3);
		itYaoKong = new ItYaoKong(ICBM.CONFIGURATION.getItem("ItemID8", ICBM.ITEM_ID_PREFIX + 7).getInt(), 4);
		itLeiSheZhiBiao = new ItLeiSheZhiBiao(ICBM.CONFIGURATION.getItem("ItemID9", ICBM.ITEM_ID_PREFIX + 8).getInt(), 6);
		itHuoLaunQi = new ItHuoLuanQi(ICBM.CONFIGURATION.getItem("ItemID10", ICBM.ITEM_ID_PREFIX + 9).getInt(), 7);
		itGenZongQi = new ItGenZongQi(ICBM.CONFIGURATION.getItem("ItemID11", ICBM.ITEM_ID_PREFIX + 10).getInt());
		itFaSheQi = new ItFaSheQi(ICBM.CONFIGURATION.getItem("ItemID12", ICBM.ITEM_ID_PREFIX + 11).getInt());

		itShouLiuDan = new ItShouLiuDan(ICBM.CONFIGURATION.getItem("ItemID13", ICBM.ITEM_ID_PREFIX + 12).getInt(), 64);
		itZiDan = new ItZiDan(ICBM.CONFIGURATION.getItem("ItemID14", ICBM.ITEM_ID_PREFIX + 13).getInt(), 80);

		itChe = new ItChe(ICBM.CONFIGURATION.getItem("ItemID15", ICBM.ITEM_ID_PREFIX + 11).getInt(), 135);

		liuGenData = new GenLiu("Sulfur Ore", "oreSulfur", new ItemStack(bLiu), 0, 40, 25, 15).enable(ICBM.CONFIGURATION);
		ICBM.CONFIGURATION.save();

		ICBMTab.itemStack = new ItemStack(ZhuYaoExplosion.bZhaDan);

		BlockDispenser.dispenseBehaviorRegistry.putObject(ZhuYaoExplosion.itShouLiuDan, new IBehaviorDispenseItem()
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

		BlockDispenser.dispenseBehaviorRegistry.putObject(ZhuYaoExplosion.itChe, new IBehaviorDispenseItem()
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
						if (var14 != 0 || !BlockRail.isRailBlock(var4.getBlockId(var11, var12 - 1, var13)))
						{
							return this.defaultItemDispenseBehavior.dispense(blockSource, itemStack);
						}

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
		GameRegistry.registerBlock(bLiu, "bLiu");
		GameRegistry.registerBlock(bBuoLiPan, "bBuoLiPan");
		GameRegistry.registerBlock(bBuoLiEnNiu, "bBuoLiEnNiu");
		GameRegistry.registerBlock(bZhaDan, IBZhaDan.class, "bZhaDan");
		GameRegistry.registerBlock(bJiQi, IBJiQi.class, "bJiQi");
		GameRegistry.registerBlock(bYinGanQi, "bYinGanQi");
		GameRegistry.registerBlock(bJia, "bJia");
		GameRegistry.registerBlock(bYinXing, "bYinXing");
		GameRegistry.registerBlock(bZha, IBZha.class, "bZha");

		if (OreDictionary.getOres("blockRadioactive").size() > 0)
		{
			bFuShe = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
			System.out.println("Detected radioative block from another mod, utilizing it.");
		}

		OreDictionary.registerOre("dustSulfur", itLiu);

		OreGenerator.addOre(liuGenData);

		ForgeChunkManager.setForcedChunkLoadingCallback(this, new LoadingCallback()
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
		});

		MinecraftForge.EVENT_BUS.register(this);
		ICBM.explosionManager = ZhaPin.class;

		UpdateNotifier.INSTANCE.checkUpdate(ICBM.NAME, ICBM.VERSION, "http://calclavia.com/downloads/icbm/recommendedversion.txt");

		this.proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		if (bFuShe == null)
		{
			ICBM.CONFIGURATION.load();
			bFuShe = new BlockRadioactive(ICBM.CONFIGURATION.getBlock("Radioactive Block", BlockRadioactive.RECOMMENDED_ID).getInt(), 4, ZhuYaoExplosion.BLOCK_TEXTURE_FILE);
			ICBM.CONFIGURATION.save();
			GameRegistry.registerBlock(bFuShe, "Radioactive");
		}
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent evt)
	{
		for (int i = 0; i < ZhaPin.list.length; i++)
		{
			if (ZhaPin.list[i] != null)
			{
				ZhaPin.list[i].init();
			}
		}

		/**
		 * Add all Recipes
		 */
		// Spikes
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 6), new Object[] { "CCC", "BBB", 'C', Block.cactus, 'B', "ingotBronze" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bZha, 6), new Object[] { "CCC", "BBB", 'C', Block.cactus, 'B', Item.ingotIron }));

		GameRegistry.addRecipe(new ItemStack(bZha, 1, 1), new Object[] { "E", "S", 'E', itDu, 'S', bZha });

		GameRegistry.addRecipe(new ItemStack(bZha, 1, 2), new Object[] { "E", "S", 'E', itLiu, 'S', bZha });

		// Rocket Launcher
		GameRegistry.addRecipe(new ShapedOreRecipe(itFaSheQi, new Object[] { "SCR", "SB ", 'R', itLeiDaQiang, 'C', new ItemStack(bJiQi, 1, JiQi.XiaoFaSheQi.ordinal() + 6), 'B', Block.stoneButton, 'S', "ingotSteel" }));

		// Camouflage
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bYinXing, 12), new Object[] { "WGW", "GCG", "WGW", 'C', "basicCircuit", 'G', Block.glass, 'W', Block.cloth }));

		// Tracker
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itGenZongQi), new Object[] { " Z ", "SBS", "SCS", 'Z', Item.compass, 'C', "basicCircuit", 'B', "battery", 'S', "ingotSteel" }));

		// Glass Pressure Plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bBuoLiPan, 1, 0), new Object[] { "##", '#', Block.glass }));

		// Glass Button
		GameRegistry.addRecipe(new ItemStack(bBuoLiEnNiu, 2), new Object[] { "G", "G", 'G', Block.glass });

		// Railgun Bullet
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itZiDan, 4, 0), new Object[] { "@", "!", "!", '@', Item.diamond, '!', "ingotBronze" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itZiDan, 1, 1), new Object[] { "@", "!", "!", '@', "antimatterGram", '!', ZhuYaoExplosion.itZiDan }));

		// Poison Powder
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itDu, 3), new Object[] { Item.spiderEye, Item.rottenFlesh }));

		// Sulfur
		GameRegistry.addSmelting(bLiu.blockID, new ItemStack(itLiu), 0.8f);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 5), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', Item.coal }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 5), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', new ItemStack(Item.coal, 1, 1) }));

		// Radar Gun
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itLeiDaQiang), new Object[] { "@#!", " $!", "  !", '@', Block.glass, '!', "ingotSteel", '#', "basicCircuit", '$', Block.stoneButton }));
		// Remote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itYaoKong), new Object[] { "?@@", "@#$", "@@@", '@', "ingotSteel", '?', Item.redstone, '#', "advancedCircuit", '$', Block.stoneButton }));
		// Laser Designator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itLeiSheZhiBiao), new Object[] { "!  ", " ? ", "  @", '@', ZhuYaoExplosion.itYaoKong.getUncharged(), '?', "eliteCircuit", '!', ZhuYaoExplosion.itLeiDaQiang.getUncharged() }));
		// Proximity Detector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bYinGanQi), new Object[] { "SSS", "S?S", "SSS", 'S', "ingotSteel", '?', itYaoKong.getUncharged() }));
		// Signal Disrupter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itHuoLaunQi, 2), new Object[] { "WWW", "W!W", "WWW", '!', ZhuYaoExplosion.itYaoKong.getUncharged(), 'W', "copperWire" }));
		// Antidote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itYao, 6), new Object[] { "@@@", "@@@", "@@@", '@', Item.pumpkinSeeds }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itYao), new Object[] { "@@@", "@@@", "@@@", '@', Item.seeds }));
		// Defuser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itJieJa), new Object[] { "I  ", " W ", "  C", 'C', "advancedCircuit", 'W', "wrench", 'I', "copperWire" }));
		// Missile Launcher Platform
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 0), new Object[] { "! !", "!C!", "!!!", '!', "ingotBronze", 'C', "basicCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 1), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(ZhuYaoExplosion.bJiQi, 1, 0), '!', "ingotSteel", 'C', "advancedCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 2), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(ZhuYaoExplosion.bJiQi, 1, 1), '!', "plateSteel", 'C', "eliteCircuit" }));
		// Missile Launcher Panel
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 3), new Object[] { "!!!", "!#!", "!?!", '#', "basicCircuit", '!', Block.glass, '?', "copperWire" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 4), new Object[] { "!$!", "!#!", "!?!", '#', "advancedCircuit", '!', "ingotSteel", '?', "copperWire", '$', new ItemStack(ZhuYaoExplosion.bJiQi, 1, 3) }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 5), new Object[] { "!$!", "!#!", "!?!", '#', "eliteCircuit", '!', Item.ingotGold, '?', "copperWire", '$', new ItemStack(ZhuYaoExplosion.bJiQi, 1, 4) }));
		// Missile Launcher Support Frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 6), new Object[] { "! !", "!!!", "! !", '!', "ingotBronze" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 7), new Object[] { "! !", "!@!", "! !", '!', "ingotSteel", '@', new ItemStack(ZhuYaoExplosion.bJiQi, 1, 6) }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 8), new Object[] { "! !", "!@!", "! !", '!', "plateSteel", '@', new ItemStack(ZhuYaoExplosion.bJiQi, 1, 7) }));
		// Radar Station
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 9), new Object[] { "?@?", " ! ", "!#!", '@', ZhuYaoExplosion.itLeiDaQiang.getUncharged(), '!', "plateSteel", '#', "basicCircuit", '?', Item.ingotGold }));
		// EMP Tower
		RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 10), new Object[] { "?W?", "@!@", "?#?", '?', "plateSteel", '!', "eliteCircuit", '@', "batteryBox", '#', "motor", 'W', "copperWire" }), "EMP Tower", ICBM.CONFIGURATION, true);
		// Railgun
		RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 11), new Object[] { "?!#", "@@@", '@', "plateSteel", '!', ZhuYaoExplosion.itLeiDaQiang.getUncharged(), '#', Item.diamond, '?', "eliteCircuit" }), "Railgun", ICBM.CONFIGURATION, true);
		// Cruise Launcher
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.bJiQi, 1, 12), new Object[] { "?! ", "@@@", '@', "plateSteel", '!', new ItemStack(ZhuYaoExplosion.bJiQi, 1, 2), '?', new ItemStack(ZhuYaoExplosion.bJiQi, 1, 8) }));

		// Missile Module
		// Find and try to add a recipe with fuel, then oil then coal.
		try
		{
			if (LiquidDictionary.getLiquid("Fuel", 1) != null)
			{
				for (LiquidContainerData data : LiquidContainerRegistry.getRegisteredLiquidContainerData())
				{
					if (data.stillLiquid != null)
					{
						if (data.stillLiquid.isLiquidEqual(LiquidDictionary.getLiquid("Fuel", 1)))
						{
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itTeBieDaoDan), new Object[] { " @ ", "@#@", "@?@", '@', "ingotSteel", '?', data.filled, '#', "basicCircuit" }));
						}
					}
				}
			}
			else if (LiquidDictionary.getLiquid("Oil", 1) != null)
			{
				for (LiquidContainerData data : LiquidContainerRegistry.getRegisteredLiquidContainerData())
				{
					if (data.stillLiquid != null)
					{
						if (data.stillLiquid.isLiquidEqual(LiquidDictionary.getLiquid("Oil", 1)))
						{
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itTeBieDaoDan), new Object[] { " @ ", "@#@", "@?@", '@', "ingotSteel", '?', data.filled, '#', "basicCircuit" }));
						}
					}
				}
			}
			else
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itTeBieDaoDan), new Object[] { " @ ", "@#@", "@?@", '@', "ingotSteel", '?', Item.coal, '#', "basicCircuit" }));
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to add missile module recipe!");
			e.printStackTrace();
		}

		// Anti-ballistic
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 1), new Object[] { "!", "?", "@", '@', new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 0), '?', new ItemStack(ZhuYaoExplosion.bZhaDan, 1, 0), '!', "basicCircuit" }));
		// Cluster
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 2), new Object[] { " ! ", " ? ", "!@!", '@', new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 0), '?', DaoDan.list[ZhaPin.qunDan.getID()].getItemStack(), '!', new ItemStack(ZhuYaoExplosion.itDaoDan, 1, 0) }));
		// Nuclear Cluster
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 3), new Object[] { " N ", "NCN", 'C', new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 2), 'N', ZhaPin.yuanZi.getItemStack() }));
		// Homing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 4), new Object[] { " B ", " C ", "BMB", 'M', new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 0), 'C', "basicCircuit", 'B', "ingotBronze" }));

		/**
		 * Add all explosive recipes.
		 */
		for (int i = 0; i < ZhaPin.E_SI_ID; i++)
		{
			// Missile
			RecipeHelper.addRecipe(new ShapelessOreRecipe(new ItemStack(ZhuYaoExplosion.itDaoDan, 1, i), new Object[] { new ItemStack(ZhuYaoExplosion.itTeBieDaoDan, 1, 0), new ItemStack(ZhuYaoExplosion.bZhaDan, 1, i) }), ZhaPin.list[i].getName() + " Missile", ICBM.CONFIGURATION, true);

			if (i < ZhaPin.E_YI_ID)
			{
				// Grenade
				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itShouLiuDan, 1, i), new Object[] { "?", "@", '@', new ItemStack(ZhuYaoExplosion.bZhaDan, 1, i), '?', Item.silk }), ZhaPin.list[i].getName() + " Grenade", ICBM.CONFIGURATION, true);
			}

			if (i < ZhaPin.E_ER_ID)
			{
				// Minecart
				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itChe, 1, i), new Object[] { "?", "@", '?', new ItemStack(ZhuYaoExplosion.bZhaDan, 1, i), '@', Item.minecartEmpty }), ZhaPin.list[i].getName() + " Minecart", ICBM.CONFIGURATION, true);
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
