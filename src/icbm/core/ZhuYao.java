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
import calclavia.lib.UniversalRecipes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Main class for ICBM core to run on. The core will need to be initialized by each ICBM module.
 * 
 * @author Calclavia
 * 
 */
@ModstatInfo(prefix = "icbm", name = ICBM.NAME, version = ICBM.VERSION)
public class ZhuYao
{
	public static final ZhuYao INSTANCE = new ZhuYao();

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

	private boolean isInitialized;

	/**
	 * Configuration file for ICBM.
	 */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity/ICBM.cfg"));

	public static final Logger LOGGER = Logger.getLogger(ICBM.NAME);

	public void init()
	{
		if (!isInitialized)
		{
			Modstats.instance().getReporter().registerMod(this);
			MinecraftForge.EVENT_BUS.register(this);

			LOGGER.fine("Loaded " + TranslationHelper.loadLanguages(YU_YAN_PATH, YU_YAN) + " languages.");

			ZhuYao.CONFIGURATION.load();
			ZAI_KUAI = ZhuYao.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", true).getBoolean(true);
			DAO_DAN_ZUI_YUAN = ZhuYao.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", 10000).getInt(10000);

			// BLOCKS
			bLiu = new BLiu(ICBM.BLOCK_ID_PREFIX + 0);

			itDu = new ItICBM(ICBM.ITEM_ID_PREFIX + 0, "poisonPowder");
			itLiu = new ItICBM(ICBM.ITEM_ID_PREFIX + 1, "sulfur");

			// -- Registering Blocks
			GameRegistry.registerBlock(bLiu, "bLiu");

			liuGenData = new GenLiu("Sulfur Ore", "oreSulfur", new ItemStack(bLiu), 0, 40, 25, 15).enable(ZhuYao.CONFIGURATION);

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
				bFuShe = new BlockRadioactive(ZhuYao.CONFIGURATION.getBlock("Radioactive Block", BlockRadioactive.RECOMMENDED_ID).getInt()).setUnlocalizedName(PREFIX + "radioactive");
				GameRegistry.registerBlock(bFuShe, "Radioactive");
				OreDictionary.registerOre("blockRadioactive", bFuShe);
				LOGGER.fine("Cannot find radioactive block in ore dictionary. Creating one.");
			}

			/**
			 * Decrease Obsidian Resistance
			 */
			Block.obsidian.setResistance(ZhuYao.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45).getInt(45));
			LOGGER.fine("Changed obsidian explosive resistance to: " + Block.obsidian.getExplosionResistance(null));

			ZhuYao.CONFIGURATION.save();

			OreDictionary.registerOre("dustSulfur", itLiu);
			OreGenerator.addOre(liuGenData);

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

			GameRegistry.registerTileEntity(TileEntityMulti.class, "ICBMMulti");
			this.isInitialized = true;
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
}