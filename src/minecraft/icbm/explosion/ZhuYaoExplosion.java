package icbm.explosion;

import icbm.api.ICBM;
import icbm.api.ICBMTab;
import icbm.api.flag.FlagRegistry;
import icbm.core.ICBMPacketManager;
import icbm.core.ZhuYao;
import icbm.explosion.cart.EChe;
import icbm.explosion.cart.ItChe;
import icbm.explosion.daodan.DaoDan;
import icbm.explosion.daodan.EDaoDan;
import icbm.explosion.daodan.ItDaoDan;
import icbm.explosion.daodan.ItTeBieDaoDan;
import icbm.explosion.dianqi.ItFaSheQi;
import icbm.explosion.dianqi.ItJieJa;
import icbm.explosion.dianqi.ItLeiDaQiang;
import icbm.explosion.dianqi.ItLeiSheZhiBiao;
import icbm.explosion.dianqi.ItYaoKong;
import icbm.explosion.jiqi.BJiQi;
import icbm.explosion.jiqi.BJiQi.JiQi;
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
import icbm.explosion.zhapin.ZhaPin.ZhaPinType;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRail;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.multiblock.BlockMulti;
import atomicscience.api.PoisonRadiation;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ZhuYaoExplosion.NAME, name = ZhuYaoExplosion.NAME, version = ICBM.VERSION, dependencies = "after:ICBM")
@NetworkMod(channels = ZhuYaoExplosion.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = ICBMPacketManager.class)
public class ZhuYaoExplosion
{
	public static final String NAME = ICBM.NAME + "|Explosion";
	public static final String CHANNEL = ICBM.NAME;

	@Instance(ZhuYaoExplosion.NAME)
	public static ZhuYaoExplosion instance;

	@SidedProxy(clientSide = "icbm.explosion.ClientProxy", serverSide = "icbm.explosion.CommonProxy")
	public static CommonProxy proxy;
	public static Item Du;
	public static final int ENTITY_ID_PREFIX = 50;

	/**
	 * Settings and Configurations
	 */
	// Blocks
	public static Block bZhaDan;
	public static Block bJiQi;
	public static BlockMulti bJia;

	// Items
	public static Item itDaoDan;
	public static Item itTeBieDaoDan;

	public static ItemElectric itJieJa;
	public static ItemElectric itLeiDaQiang;
	public static ItemElectric itYaoKong;
	public static ItemElectric itLeiSheZhiBiao;
	public static ItemElectric itFaSheQi;

	public static Item itShouLiuDan;
	public static Item itZiDan;
	public static Item itChe;

	public static final Du DU_DU = new Du("Chemical", 1, false);
	public static final Du DU_CHUAN_RAN = new Du("Contagious", 1, true);

	/**
	 * Flags used for protection commands.
	 */
	private static final String QIZI_QUAN_BU = FlagRegistry.registerFlag("ban_icbm");
	private static final String QIZI_ZHA_DAN = FlagRegistry.registerFlag("ban_explosive");
	private static final String QIZI_SHOU_LIU_DAN = FlagRegistry.registerFlag("ban_grenade");
	private static final String QIZI_DAO_DAN = FlagRegistry.registerFlag("ban_missile");
	private static final String QIZI_CHE = FlagRegistry.registerFlag("ban_minecart");

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(this, this.proxy);

		ICBM.CONFIGURATION.load();

		bZhaDan = new BZhaDan(ICBM.CONFIGURATION.getBlock("BlockID3", ICBM.BLOCK_ID_PREFIX + 3).getInt(), 16);
		bJiQi = new BJiQi(ICBM.CONFIGURATION.getBlock("BlockID4", ICBM.BLOCK_ID_PREFIX + 4).getInt());
		bJia = new BlockMulti(ICBM.CONFIGURATION.getBlock("BlockID6", ICBM.BLOCK_ID_PREFIX + 6).getInt());

		// ITEMS
		itDaoDan = new ItDaoDan(ICBM.CONFIGURATION.getItem("ItemID4", ICBM.ITEM_ID_PREFIX + 3).getInt(), 32);
		itTeBieDaoDan = new ItTeBieDaoDan(ICBM.CONFIGURATION.getItem("ItemID5", ICBM.ITEM_ID_PREFIX + 4).getInt(), 32);

		itJieJa = new ItJieJa(ICBM.CONFIGURATION.getItem("ItemID6", ICBM.ITEM_ID_PREFIX + 5).getInt(), 5);
		itLeiDaQiang = new ItLeiDaQiang(ICBM.CONFIGURATION.getItem("ItemID7", ICBM.ITEM_ID_PREFIX + 6).getInt(), 3);
		itYaoKong = new ItYaoKong(ICBM.CONFIGURATION.getItem("ItemID8", ICBM.ITEM_ID_PREFIX + 7).getInt(), 4);
		itLeiSheZhiBiao = new ItLeiSheZhiBiao(ICBM.CONFIGURATION.getItem("ItemID9", ICBM.ITEM_ID_PREFIX + 8).getInt(), 6);
		itFaSheQi = new ItFaSheQi(ICBM.CONFIGURATION.getItem("ItemID12", ICBM.ITEM_ID_PREFIX + 11).getInt());

		itShouLiuDan = new ItShouLiuDan(ICBM.CONFIGURATION.getItem("ItemID13", ICBM.ITEM_ID_PREFIX + 12).getInt(), 64);
		itZiDan = new ItZiDan(ICBM.CONFIGURATION.getItem("ItemID14", ICBM.ITEM_ID_PREFIX + 13).getInt(), 80);

		itChe = new ItChe(ICBM.CONFIGURATION.getItem("ItemID15", ICBM.ITEM_ID_PREFIX + 11).getInt(), 135);

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
					EnumFacing enumFacing = EnumFacing.getFront(blockSource.func_82620_h());

					EShouLiuDan entity = new EShouLiuDan(world, new Vector3(x, y, z), itemStack.getItemDamage());
					entity.setThrowableHeading((double) enumFacing.getFrontOffsetX(), 0.10000000149011612D, (double) enumFacing.getFrontOffsetZ(), 0.5F, 1.0F);
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

					EnumFacing var3 = EnumFacing.getFront(blockSource.func_82620_h());
					World var4 = blockSource.getWorld();
					double var5 = blockSource.getX() + (double) ((float) var3.getFrontOffsetX() * 1.125F);
					double var7 = blockSource.getY();
					double var9 = blockSource.getZ() + (double) ((float) var3.getFrontOffsetZ() * 1.125F);
					int var11 = blockSource.getXInt() + var3.getFrontOffsetX();
					int var12 = blockSource.getYInt();
					int var13 = blockSource.getZInt() + var3.getFrontOffsetZ();
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
		GameRegistry.registerBlock(bZhaDan, IBZhaDan.class, "bZhaDan");
		GameRegistry.registerBlock(bJiQi, IBJiQi.class, "bJiQi");
		GameRegistry.registerBlock(bJia, "bJia");

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

		ICBM.explosionManager = ZhaPin.class;

		this.proxy.preInit();
	}

	@Init
	public void load(FMLInitializationEvent evt)
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
		// Rocket Launcher
		GameRegistry.addRecipe(new ShapedOreRecipe(itFaSheQi, new Object[] { "SCR", "SB ", 'R', itLeiDaQiang, 'C', new ItemStack(bJiQi, 1, JiQi.XiaoFaSheQi.ordinal() + 6), 'B', Block.stoneButton, 'S', "ingotSteel" }));

		// Railgun Bullet
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itZiDan, 4, 0), new Object[] { "@", "!", "!", '@', Item.diamond, '!', "ingotBronze" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itZiDan, 1, 1), new Object[] { "@", "!", "!", '@', "antimatterGram", '!', ZhuYaoExplosion.itZiDan }));

		// Radar Gun
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itLeiDaQiang), new Object[] { "@#!", " $!", "  !", '@', Block.glass, '!', "ingotSteel", '#', "basicCircuit", '$', Block.stoneButton }));
		// Remote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itYaoKong), new Object[] { "?@@", "@#$", "@@@", '@', "ingotSteel", '?', Item.redstone, '#', "advancedCircuit", '$', Block.stoneButton }));
		// Laser Designator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoExplosion.itLeiSheZhiBiao), new Object[] { "!  ", " ? ", "  @", '@', ZhuYaoExplosion.itYaoKong.getUncharged(), '?', "eliteCircuit", '!', ZhuYaoExplosion.itLeiDaQiang.getUncharged() }));
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

	/**
	 * Is a specific position being protected from a specific type of danger?
	 */
	public static boolean shiBaoHu(World world, Vector3 diDian, ZhaPinType type, ZhaPin zhaPin)
	{
		if (ZhuYao.BAO_HU.containsValue(world, QIZI_QUAN_BU, "true", diDian))
		{
			return true;
		}

		switch (type)
		{
			case QUAN_BU:
				return ZhuYao.BAO_HU.containsValue(world, QIZI_CHE, "true", diDian) || ZhuYao.BAO_HU.containsValue(world, QIZI_DAO_DAN, "true", diDian) || ZhuYao.BAO_HU.containsValue(world, QIZI_SHOU_LIU_DAN, "true", diDian) || ZhuYao.BAO_HU.containsValue(world, QIZI_ZHA_DAN, "true", diDian);
			case CHE:
				return ZhuYao.BAO_HU.containsValue(world, QIZI_CHE, "true", diDian);
			case DAO_DAN:
				return ZhuYao.BAO_HU.containsValue(world, QIZI_DAO_DAN, "true", diDian);
			case SHOU_LIU_DAN:
				return ZhuYao.BAO_HU.containsValue(world, QIZI_SHOU_LIU_DAN, "true", diDian);
			case ZHA_DAN:
				return ZhuYao.BAO_HU.containsValue(world, QIZI_ZHA_DAN, "true", diDian);
		}

		return ZhuYao.BAO_HU.containsValue(world, zhaPin.qiZi, "true", diDian);
	}

	public static boolean shiBaoHu(World world, Vector3 diDian, ZhaPinType type, int zhaPinID)
	{
		if (zhaPinID < ZhaPin.list.length && zhaPinID > 0)
		{
			return shiBaoHu(world, diDian, type, ZhaPin.list[zhaPinID]);
		}

		return false;
	}
}
