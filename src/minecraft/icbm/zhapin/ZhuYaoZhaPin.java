package icbm.zhapin;

import icbm.api.ICBM;
import icbm.api.ICBMFlags;
import icbm.api.ICBMTab;
import icbm.core.ZhuYao;
import icbm.zhapin.cart.EChe;
import icbm.zhapin.cart.ItChe;
import icbm.zhapin.daodan.DaoDan;
import icbm.zhapin.daodan.EDaoDan;
import icbm.zhapin.daodan.ItDaoDan;
import icbm.zhapin.daodan.ItTeBieDaoDan;
import icbm.zhapin.dianqi.ItFaSheQi;
import icbm.zhapin.dianqi.ItJieJa;
import icbm.zhapin.dianqi.ItLeiDaQiang;
import icbm.zhapin.dianqi.ItLeiSheZhiBiao;
import icbm.zhapin.dianqi.ItYaoKong;
import icbm.zhapin.jiqi.BJiQi;
import icbm.zhapin.jiqi.BJiQi.JiQi;
import icbm.zhapin.jiqi.IBJiQi;
import icbm.zhapin.po.PChuanRanDu;
import icbm.zhapin.po.PDaDu;
import icbm.zhapin.po.PDongShang;
import icbm.zhapin.zhapin.BZhaDan;
import icbm.zhapin.zhapin.EShouLiuDan;
import icbm.zhapin.zhapin.EZhaDan;
import icbm.zhapin.zhapin.EZhaPin;
import icbm.zhapin.zhapin.IBZhaDan;
import icbm.zhapin.zhapin.ItShouLiuDan;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.ZhaPin.ZhaPinType;

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
import universalelectricity.prefab.flag.FlagRegistry;
import universalelectricity.prefab.multiblock.BlockMulti;
import atomicscience.api.PoisonRadiation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
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
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ZhuYaoZhaPin.NAME, name = ZhuYaoZhaPin.NAME, version = ICBM.VERSION, dependencies = "after:ICBM|Sentry;after:BasicComponents;after:AtomicScience")
@NetworkMod(channels = ZhuYaoZhaPin.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = ZhaPinPacketGuanLi.class)
public class ZhuYaoZhaPin
{
	public static final String NAME = ICBM.NAME + "|Explosion";
	public static final String CHANNEL = ICBM.NAME + "|E";

	@Instance(ZhuYaoZhaPin.NAME)
	public static ZhuYaoZhaPin instance;

	@SidedProxy(clientSide = "icbm.zhapin.ClientProxy", serverSide = "icbm.zhapin.CommonProxy")
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
	public static Item itChe;

	public static final Du DU_DU = new Du("Chemical", 1, false);
	public static final Du DU_CHUAN_RAN = new Du("Contagious", 1, true);

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		ZhuYao.INSTANCE.init();
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
		itChe = new ItChe(ICBM.CONFIGURATION.getItem("ItemID15", ICBM.ITEM_ID_PREFIX + 11).getInt(), 135);

		ICBM.CONFIGURATION.save();

		ICBMTab.itemStack = new ItemStack(ZhuYaoZhaPin.bZhaDan);

		BlockDispenser.dispenseBehaviorRegistry.putObject(ZhuYaoZhaPin.itShouLiuDan, new IBehaviorDispenseItem()
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

		BlockDispenser.dispenseBehaviorRegistry.putObject(ZhuYaoZhaPin.itChe, new IBehaviorDispenseItem()
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

		// Radar Gun
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itLeiDaQiang), new Object[] { "@#!", " $!", "  !", '@', Block.glass, '!', "ingotSteel", '#', "basicCircuit", '$', Block.stoneButton }));
		// Remote
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itYaoKong), new Object[] { "?@@", "@#$", "@@@", '@', "ingotSteel", '?', Item.redstone, '#', "advancedCircuit", '$', Block.stoneButton }));
		// Laser Designator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itLeiSheZhiBiao), new Object[] { "!  ", " ? ", "  @", '@', ZhuYaoZhaPin.itYaoKong.getUncharged(), '?', "eliteCircuit", '!', ZhuYaoZhaPin.itLeiDaQiang.getUncharged() }));
		// Defuser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itJieJa), new Object[] { "I  ", " W ", "  C", 'C', "advancedCircuit", 'W', "wrench", 'I', "copperWire" }));
		// Missile Launcher Platform
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 0), new Object[] { "! !", "!C!", "!!!", '!', "ingotBronze", 'C', "basicCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 1), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 0), '!', "ingotSteel", 'C', "advancedCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 2), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 1), '!', "plateSteel", 'C', "eliteCircuit" }));
		// Missile Launcher Panel
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 3), new Object[] { "!!!", "!#!", "!?!", '#', "basicCircuit", '!', Block.glass, '?', "copperWire" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 4), new Object[] { "!$!", "!#!", "!?!", '#', "advancedCircuit", '!', "ingotSteel", '?', "copperWire", '$', new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 3) }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 5), new Object[] { "!$!", "!#!", "!?!", '#', "eliteCircuit", '!', Item.ingotGold, '?', "copperWire", '$', new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 4) }));
		// Missile Launcher Support Frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 6), new Object[] { "! !", "!!!", "! !", '!', "ingotBronze" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 7), new Object[] { "! !", "!@!", "! !", '!', "ingotSteel", '@', new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 6) }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 8), new Object[] { "! !", "!@!", "! !", '!', "plateSteel", '@', new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 7) }));
		// Radar Station
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 9), new Object[] { "?@?", " ! ", "!#!", '@', ZhuYaoZhaPin.itLeiDaQiang.getUncharged(), '!', "plateSteel", '#', "basicCircuit", '?', Item.ingotGold }));
		// EMP Tower
		RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 10), new Object[] { "?W?", "@!@", "?#?", '?', "plateSteel", '!', "eliteCircuit", '@', "batteryBox", '#', "motor", 'W', "copperWire" }), "EMP Tower", ICBM.CONFIGURATION, true);
		// Cruise Launcher
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 11), new Object[] { "?! ", "@@@", '@', "plateSteel", '!', new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 2), '?', new ItemStack(ZhuYaoZhaPin.bJiQi, 1, 8) }));

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
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan), new Object[] { " @ ", "@#@", "@?@", '@', "ingotSteel", '?', data.filled, '#', "basicCircuit" }));
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
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan), new Object[] { " @ ", "@#@", "@?@", '@', "ingotSteel", '?', data.filled, '#', "basicCircuit" }));
						}
					}
				}
			}
			else
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan), new Object[] { " @ ", "@#@", "@?@", '@', "ingotSteel", '?', Item.coal, '#', "basicCircuit" }));
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to add missile module recipe!");
			e.printStackTrace();
		}

		// Anti-ballistic
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 1), new Object[] { "!", "?", "@", '@', new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 0), '?', new ItemStack(ZhuYaoZhaPin.bZhaDan, 1, 0), '!', "basicCircuit" }));
		// Cluster
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 2), new Object[] { " ! ", " ? ", "!@!", '@', new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 0), '?', DaoDan.list[ZhaPin.qunDan.getID()].getItemStack(), '!', new ItemStack(ZhuYaoZhaPin.itDaoDan, 1, 0) }));
		// Nuclear Cluster
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 3), new Object[] { " N ", "NCN", 'C', new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 2), 'N', ZhaPin.yuanZi.getItemStack() }));
		// Homing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 4), new Object[] { " B ", " C ", "BMB", 'M', new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 0), 'C', "basicCircuit", 'B', "ingotBronze" }));

		/**
		 * Add all explosive recipes.
		 */
		for (int i = 0; i < ZhaPin.E_SI_ID; i++)
		{
			// Missile
			RecipeHelper.addRecipe(new ShapelessOreRecipe(new ItemStack(ZhuYaoZhaPin.itDaoDan, 1, i), new Object[] { new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, 0), new ItemStack(ZhuYaoZhaPin.bZhaDan, 1, i) }), ZhaPin.list[i].getName() + " Missile", ICBM.CONFIGURATION, true);

			if (i < ZhaPin.E_YI_ID)
			{
				// Grenade
				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itShouLiuDan, 1, i), new Object[] { "?", "@", '@', new ItemStack(ZhuYaoZhaPin.bZhaDan, 1, i), '?', Item.silk }), ZhaPin.list[i].getName() + " Grenade", ICBM.CONFIGURATION, true);
			}

			if (i < ZhaPin.E_ER_ID)
			{
				// Minecart
				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(ZhuYaoZhaPin.itChe, 1, i), new Object[] { "?", "@", '?', new ItemStack(ZhuYaoZhaPin.bZhaDan, 1, i), '@', Item.minecartEmpty }), ZhaPin.list[i].getName() + " Minecart", ICBM.CONFIGURATION, true);
			}
		}

		EntityRegistry.registerGlobalEntityID(EZhaDan.class, "ICBMExplosive", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EDaoDan.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EZhaPin.class, "ICBMProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EFeiBlock.class, "ICBMGravityBlock", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EGuang.class, "ICBMLightBeam", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(ESuiPian.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EShouLiuDan.class, "ICBMGrenade", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EChe.class, "ICBMChe", EntityRegistry.findGlobalUniqueEntityId());

		EntityRegistry.registerModEntity(EZhaDan.class, "ICBMExplosive", ENTITY_ID_PREFIX, this, 50, 5, true);
		EntityRegistry.registerModEntity(EDaoDan.class, "ICBMMissile", ENTITY_ID_PREFIX + 1, this, 100, 1, true);
		EntityRegistry.registerModEntity(EZhaPin.class, "ICBMProceduralExplosion", ENTITY_ID_PREFIX + 2, this, 100, 5, true);
		EntityRegistry.registerModEntity(EFeiBlock.class, "ICBMGravityBlock", ENTITY_ID_PREFIX + 3, this, 50, 15, true);
		EntityRegistry.registerModEntity(EGuang.class, "ICBMLightBeam", ENTITY_ID_PREFIX + 4, this, 80, 5, true);
		EntityRegistry.registerModEntity(ESuiPian.class, "ICBMFragment", ENTITY_ID_PREFIX + 5, this, 40, 8, true);
		EntityRegistry.registerModEntity(EShouLiuDan.class, "ICBMGrenade", ENTITY_ID_PREFIX + 6, this, 50, 5, true);
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
		if (FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME) != null)
		{
			if (FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_GLOBAL, "true", diDian))
			{
				return true;
			}

			boolean baoHu = false;

			switch (type)
			{
				case QUAN_BU:
					baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_MINECART, "true", diDian) || FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_MISSILE, "true", diDian) || FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_GRENADE, "true", diDian) || FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_EXPLOSIVE, "true", diDian);
					break;
				case CHE:
					baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_MINECART, "true", diDian);
					break;
				case DAO_DAN:
					baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_MISSILE, "true", diDian);
					break;
				case SHOU_LIU_DAN:
					baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_GRENADE, "true", diDian);
					break;
				case ZHA_DAN:
					baoHu = FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, ICBMFlags.FLAG_BAN_EXPLOSIVE, "true", diDian);
					break;
			}

			return FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).containsValue(world, zhaPin.qiZi, "true", diDian) || baoHu;
		}

		return false;
	}

	public static boolean shiBaoHu(World world, Vector3 diDian, ZhaPinType type, int zhaPinID)
	{
		if (zhaPinID < ZhaPin.list.length && zhaPinID >= 0)
		{
			return shiBaoHu(world, diDian, type, ZhaPin.list[zhaPinID]);
		}

		return false;
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		ZhuYao.INSTANCE.serverStarting(event);
		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
		serverCommandManager.registerCommand(new MingLing());
	}
}
