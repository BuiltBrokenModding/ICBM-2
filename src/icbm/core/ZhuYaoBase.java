package icbm.core;

import icbm.api.ICBM;
import icbm.core.di.ItICBM;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

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

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.flag.CommandFlag;
import universalelectricity.prefab.flag.FlagRegistry;
import universalelectricity.prefab.flag.ModFlag;
import universalelectricity.prefab.flag.NBTFileLoader;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.ore.OreGenBase;
import universalelectricity.prefab.ore.OreGenerator;
import atomicscience.api.BlockRadioactive;
import basiccomponents.common.BasicComponents;
import calclavia.lib.UniversalRecipes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Main class for ICBM core to run on. The core will need to be initialized by each ICBM module.
 * 
 * @author Calclavia
 * 
 */
@ModstatInfo(prefix = "icbm", name = ICBM.NAME, version = ICBM.VERSION)
public class ZhuYaoBase
{
	public static final ZhuYaoBase INSTANCE = new ZhuYaoBase();

	public static boolean ZAI_KUAI;

	public static Block bLiu, bFuShe;

	public static Item itLiu, itDu;

	public static OreGenBase liuGenData;

	public static final String PREFIX = "icbm:";

	public static final String RESOURCE_PATH = "/mods/icbm/";
	public static final String TEXTURE_PATH = RESOURCE_PATH + "textures/";
	public static final String GUI_PATH = TEXTURE_PATH + "gui/";
	public static final String MODEL_PATH = TEXTURE_PATH + "models/";
	public static final String SMINE_TEXTURE = MODEL_PATH + "s-mine.png";
	public static final String BLOCK_PATH = TEXTURE_PATH + "blocks/";
	public static final String ITEM_PATH = TEXTURE_PATH + "items/";

	public static final String YU_YAN_PATH = RESOURCE_PATH + "yuyan/";

	private static final String[] YU_YAN = new String[] { "en_US", "zh_CN", "es_ES" };

	public static int DAO_DAN_ZUI_YUAN;

	/**
	 * GUI ID Numbers: These numbers are used to identify the ID of the specific GUIs used by ICBM.
	 * TODO: USE TILES INSTEAD OF IDS.
	 */
	public static final int GUI_XIA_FA_SHE_QI = 1;
	public static final int GUI_FA_SHE_SHI_MUO = 2;
	public static final int GUI_LEI_DA_TAI = 3;
	public static final int GUI_YIN_GAN_QI = 4;
	public static final int GUI_SHENG_BUO = 5;
	public static final int GUI_DIAN_CI_QI = 6;
	public static final int GUI_FA_SHE_DI = 7;

	private static boolean isPreInit, isInit, isPostInit;

	/**
	 * Configuration file for ICBM.
	 */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/ICBM.cfg"));

	public static final Logger LOGGER = Logger.getLogger(ICBM.NAME);

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		if (!isPreInit)
		{
			Modstats.instance().getReporter().registerMod(INSTANCE);
			MinecraftForge.EVENT_BUS.register(INSTANCE);

			LOGGER.fine("Loaded " + TranslationHelper.loadLanguages(YU_YAN_PATH, YU_YAN) + " languages.");

			ZhuYaoBase.CONFIGURATION.load();
			ZAI_KUAI = ZhuYaoBase.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", true).getBoolean(true);
			DAO_DAN_ZUI_YUAN = ZhuYaoBase.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", 10000).getInt(10000);

			// BLOCKS
			bLiu = new BLiu(ICBM.BLOCK_ID_PREFIX + 0);

			itDu = new ItICBM(ICBM.ITEM_ID_PREFIX + 0, "poisonPowder");
			itLiu = new ItICBM(ICBM.ITEM_ID_PREFIX + 1, "sulfur");

			// -- Registering Blocks
			GameRegistry.registerBlock(bLiu, "bLiu");

			liuGenData = new GenLiu("Sulfur Ore", "oreSulfur", new ItemStack(bLiu), 0, 40, 20, 4).enable(ZhuYaoBase.CONFIGURATION);

			/**
			 * Check for existence of radioactive block. If it does not exist, then create it.
			 */
			if (OreDictionary.getOres("blockRadioactive").size() > 0)
			{
				bFuShe = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
				LOGGER.fine("Detected radioative block from another mod, utilizing it.");
			}
			else
			{
				bFuShe = new BlockRadioactive(ZhuYaoBase.CONFIGURATION.getBlock("Radioactive Block", BlockRadioactive.RECOMMENDED_ID).getInt()).setUnlocalizedName(PREFIX + "radioactive");
				GameRegistry.registerBlock(bFuShe, "Radioactive");
				OreDictionary.registerOre("blockRadioactive", bFuShe);
				LOGGER.fine("Cannot find radioactive block in ore dictionary. Creating one.");
			}

			/**
			 * Decrease Obsidian Resistance
			 */
			Block.obsidian.setResistance(ZhuYaoBase.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45).getInt(45));
			LOGGER.fine("Changed obsidian explosive resistance to: " + Block.obsidian.getExplosionResistance(null));

			ZhuYaoBase.CONFIGURATION.save();

			OreDictionary.registerOre("dustSulfur", itLiu);
			OreGenerator.addOre(liuGenData);

			GameRegistry.registerTileEntity(TileEntityMulti.class, "ICBMMulti");

			isPreInit = true;
		}
	}

	@Init
	public void init(FMLInitializationEvent event)
	{
		if (!isInit)
		{
			/**
			 * Load Basic Components
			 */
			BasicComponents.register(this, this.getChannel());

			BasicComponents.requestItem("ingotCopper", 0);
			BasicComponents.requestItem("ingotTin", 0);

			BasicComponents.requestBlock("oreCopper", 0);
			BasicComponents.requestBlock("oreTin", 0);

			BasicComponents.requestItem("ingotSteel", 0);
			BasicComponents.requestItem("dustSteel", 0);
			BasicComponents.requestItem("plateSteel", 0);

			BasicComponents.requestItem("ingotBronze", 0);
			BasicComponents.requestItem("dustBronze", 0);
			BasicComponents.requestItem("plateBronze", 0);

			BasicComponents.requestBlock("copperWire", 0);

			BasicComponents.requestItem("circuitBasic", 0);
			BasicComponents.requestItem("circuitAdvanced", 0);
			BasicComponents.requestItem("circuitElite", 0);

			BasicComponents.requestItem("motor", 0);

			isInit = true;
		}
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		if (!isPostInit)
		{
			/**
			 * LOAD.
			 */
			UniversalRecipes.init();

			// Sulfur
			GameRegistry.addSmelting(bLiu.blockID, new ItemStack(itLiu, 4), 0.8f);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 3), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', Item.coal }));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 3), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', new ItemStack(Item.coal, 1, 1) }));

			GameRegistry.addRecipe(new ShapedOreRecipe(Block.tnt, new Object[] { "@@@", "@R@", "@@@", '@', Item.gunpowder, 'R', Item.redstone }));

			// Poison Powder
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itDu, 3), new Object[] { Item.spiderEye, Item.rottenFlesh }));

			isPostInit = true;
		}
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		FlagRegistry.registerModFlag(FlagRegistry.DEFAULT_NAME, new ModFlag(NBTFileLoader.loadData(FlagRegistry.DEFAULT_NAME)));

		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
		serverCommandManager.registerCommand(new CommandFlag(FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME)));
	}

	@ForgeSubscribe
	public void worldSave(Save evt)
	{
		if (!evt.world.isRemote)
		{
			NBTFileLoader.saveData(FlagRegistry.DEFAULT_NAME, FlagRegistry.getModFlag(FlagRegistry.DEFAULT_NAME).getNBT());
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

	public static void setModMetadata(String id, ModMetadata metadata)
	{
		metadata.modId = id;
		metadata.name = ICBM.NAME;
		metadata.description = "ICBM is a Minecraft Mod that introduces intercontinental ballistic missiles to Minecraft. But the fun doesn't end there! This mod also features many different explosives, missiles and machines classified in three different tiers. If strategic warfare, carefully coordinated airstrikes, messing with matter and general destruction are up your alley, then this mod is for you!";

		metadata.url = "http://www.universalelectricity.com/icbm/";

		metadata.logoFile = "/icbm_logo.png";
		metadata.version = ICBM.VERSION;
		metadata.authorList = Arrays.asList(new String[] { "Calclavia" });
		metadata.credits = "Please visit the website.";
		metadata.autogenerated = false;
	}

	protected String getChannel()
	{
		return null;
	}
}