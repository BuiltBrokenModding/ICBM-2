package icbm.core;

import icbm.api.ICBM;
import icbm.api.ICBMTab;
import icbm.api.flag.CommandFlag;
import icbm.api.flag.FlagRegistry;
import icbm.api.flag.ModFlagData;
import icbm.api.flag.NBTFileLoader;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.UpdateNotifier;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.ore.OreGenBase;
import universalelectricity.prefab.ore.OreGenerator;
import atomicscience.api.BlockRadioactive;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Main class for ICBM core to run on. Treat as a core mod that all other modules are dependent from
 * but remain packaged in each module for distribution.
 * 
 * @author Calclavia
 * 
 */

@Mod(modid = ICBM.NAME, name = ICBM.NAME, version = ICBM.VERSION, dependencies = "after:BasicComponents;after:AtomicScience")
@NetworkMod(channels = ZhuYao.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = ICBMPacketManager.class)
public class ZhuYao
{
	@Instance("ICBM")
	public static ZhuYao instance;

	public static final String CHANNEL = ICBM.NAME;

	public static boolean ZAI_KUAI;

	/**
	 * Ores
	 */
	public static Block bLiu, bFuShe;

	/**
	 * Items
	 */
	public static Item itLiu, itDu;

	public static OreGenBase liuGenData;

	public static final String SMINE_TEXTURE = ICBM.TEXTURE_FILE_PATH + "S-Mine.png";

	public static final String BLOCK_TEXTURE_FILE = ICBM.TEXTURE_FILE_PATH + "blocks.png";

	public static final String ITEM_TEXTURE_FILE = ICBM.TEXTURE_FILE_PATH + "items.png";

	public static final String TRACKER_TEXTURE_FILE = ICBM.TEXTURE_FILE_PATH + "tracker.png";

	public static final String YU_YAN_PATH = "/icbm/yuyan/";

	private static final String[] YU_YAN = new String[] { "en_US", "zh_CN", "es_ES" };

	public static int DAO_DAN_ZUI_YUAN;

	public static ModFlagData BAO_HU;

	/**
	 * GUI ID Numbers: These numbers are used to identify the ID of the specific GUIs used by ICBM.
	 */
	public static final int GUI_RAIL_GUN = 0;
	public static final int GUI_XIA_FA_SHE_QI = 1;
	public static final int GUI_FA_SHE_SHI_MUO = 2;
	public static final int GUI_LEI_DA_TAI = 3;
	public static final int GUI_YIN_GAN_QI = 4;
	public static final int GUI_SHENG_BUO = 5;
	public static final int GUI_DIAN_CI_QI = 6;
	public static final int GUI_FA_SHE_DI = 7;

	public static final String QIZI_ZHA_DAN = FlagRegistry.registerFlag("ban_explosive");
	public static final String QIZI_SHOU_LIU_DAN = FlagRegistry.registerFlag("ban_grenade");
	public static final String QIZI_CHE = FlagRegistry.registerFlag("ban_minecart");
	public static final String QIZI_DAO_DAN = FlagRegistry.registerFlag("ban_missile");

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		UniversalElectricity.register(this, 1, 2, 5, false);
		MinecraftForge.EVENT_BUS.register(this);

		System.out.println(ICBM.NAME + " Loaded " + TranslationHelper.loadLanguages(YU_YAN_PATH, YU_YAN) + " languages.");

		ICBM.CONFIGURATION.load();
		ZAI_KUAI = ICBM.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", true).getBoolean(true);
		DAO_DAN_ZUI_YUAN = ICBM.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", 10000).getInt(10000);

		// BLOCKS
		bLiu = new BLiu(ICBM.CONFIGURATION.getBlock("BlockID1", ICBM.BLOCK_ID_PREFIX + 0).getInt());

		itDu = new Item(ICBM.CONFIGURATION.getItem("ItemID2", ICBM.ITEM_ID_PREFIX + 0).getInt()).setCreativeTab(ICBMTab.INSTANCE).setTextureFile(ZhuYao.ITEM_TEXTURE_FILE).setItemName("poisonPowder").setIconIndex(0);
		itLiu = new Item(ICBM.CONFIGURATION.getItem("ItemID1", ICBM.ITEM_ID_PREFIX + 1).getInt()).setCreativeTab(ICBMTab.INSTANCE).setTextureFile(ZhuYao.ITEM_TEXTURE_FILE).setItemName("sulfur").setIconIndex(1);

		// -- Registering Blocks
		GameRegistry.registerBlock(bLiu, "bLiu");

		liuGenData = new GenLiu("Sulfur Ore", "oreSulfur", new ItemStack(bLiu), 0, 40, 25, 15).enable(ICBM.CONFIGURATION);

		/**
		 * Check for existence of radioactive block. If it does not exist, then create it.
		 */
		if (OreDictionary.getOres("blockRadioactive").size() > 0)
		{
			bFuShe = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
			System.out.println(ICBM.NAME + " detected radioative block from another mod, utilizing it.");
		}
		else
		{
			bFuShe = new BlockRadioactive(ICBM.CONFIGURATION.getBlock("Radioactive Block", BlockRadioactive.RECOMMENDED_ID).getInt(), 4, ZhuYao.BLOCK_TEXTURE_FILE);
			GameRegistry.registerBlock(bFuShe, "Radioactive");
			OreDictionary.registerOre("blockRadioactive", bFuShe);
			System.out.println(ICBM.NAME + " cannot find radioactive block in ore dictionary. Creating one.");
		}

		ICBM.CONFIGURATION.save();

		OreDictionary.registerOre("dustSulfur", itLiu);
		OreGenerator.addOre(liuGenData);

		UpdateNotifier.INSTANCE.checkUpdate(ICBM.NAME, ICBM.VERSION, "http://calclavia.com/downloads/icbm/recommendedversion.txt");
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		// Sulfur
		GameRegistry.addSmelting(bLiu.blockID, new ItemStack(itLiu, 4), 0.8f);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 5), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', Item.coal }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 5), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', new ItemStack(Item.coal, 1, 1) }));

		// Poison Powder
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itDu, 3), new Object[] { Item.spiderEye, Item.rottenFlesh }));

		GameRegistry.registerTileEntity(TileEntityMulti.class, "ICBMMulti");
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		BAO_HU = new ModFlagData(NBTFileLoader.loadData("ICBM"));

		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
		serverCommandManager.registerCommand(new MingLing());
		serverCommandManager.registerCommand(new CommandFlag(BAO_HU));
	}

	@ServerStopping
	public void serverStopping(FMLServerStoppingEvent event)
	{
		NBTFileLoader.saveData("ICBM", BAO_HU.getNBT());
	}

	@ForgeSubscribe
	public void worldSave(Save evt)
	{
		if (!evt.world.isRemote)
		{
			NBTFileLoader.saveData("ICBM", BAO_HU.getNBT());
		}
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
}