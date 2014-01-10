package icbm.core;

import icbm.api.ICBM;
import icbm.core.base.ItemICBMBase;

import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.link.TileMultiBlockPart;
import calclavia.lib.ore.OreGenBase;
import calclavia.lib.ore.OreGenerator;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Main class for ICBM core to run on. The core will need to be initialized by each ICBM module.
 * 
 * @author Calclavia
 */
@ModstatInfo(prefix = "icbm", name = ICBM.NAME, version = ICBM.VERSION)
public class ICBMCore
{
	public static final ICBMCore INSTANCE = new ICBMCore();

	public static Block blockSulfurOre, blockRadioactive;

	public static Item itemSulfurDust, itemPoisonPowder;

	public static OreGenBase sulfureOreGenData;

	public static final String DOMAIN = "icbm";
	public static final String PREFIX = DOMAIN + ":";

	public static final String ASSETS_PATH = "/assets/icbm/";
	public static final String TEXTURE_PATH = "textures/";
	public static final String GUI_PATH = TEXTURE_PATH + "gui/";
	public static final String MODEL_PATH = TEXTURE_PATH + "models/";
	public static final String BLOCK_PATH = TEXTURE_PATH + "blocks/";
	public static final String ITEM_PATH = TEXTURE_PATH + "items/";

	public static final String YU_YAN_PATH = ASSETS_PATH + "yuyan/";

	private static final String[] YU_YAN = new String[] { "en_US", "zh_CN", "es_ES", "de_DE" };

	private static boolean isPreInit, isInit, isPostInit;

	public static final Logger LOGGER = Logger.getLogger(ICBM.NAME);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		if (!isPreInit)
		{
			Modstats.instance().getReporter().registerMod(INSTANCE);
			MinecraftForge.EVENT_BUS.register(INSTANCE);

			LOGGER.fine("Loaded " + LanguageUtility.loadLanguages(YU_YAN_PATH, YU_YAN) + " languages.");

			ICBMConfiguration.initiate();
			ICBMConfiguration.CONFIGURATION.load();

			// BLOCKS
			blockSulfurOre = new BlockSulfureOre(ICBM.BLOCK_ID_PREFIX + 0);

			// Items
			itemPoisonPowder = new ItemICBMBase(ICBM.ITEM_ID_PREFIX + 0, "poisonPowder");
			itemSulfurDust = new ItemICBMBase(ICBM.ITEM_ID_PREFIX + 1, "sulfur");

			// -- Registering Blocks
			GameRegistry.registerBlock(blockSulfurOre, "blockSulferOre");

			sulfureOreGenData = new OreGeneratorICBM("Sulfur Ore", "oreSulfur", new ItemStack(blockSulfurOre), 0, 40, 20, 4).enable(ICBMConfiguration.CONFIGURATION);

			/** Check for existence of radioactive block. If it does not exist, then create it. */
			if (OreDictionary.getOres("blockRadioactive").size() > 0)
			{
				blockRadioactive = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
				LOGGER.fine("Detected radioative block from another mod, utilizing it.");
			}

			/** Decrease Obsidian Resistance */
			Block.obsidian.setResistance(ICBMConfiguration.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45).getInt(45));
			LOGGER.fine("Changed obsidian explosive resistance to: " + Block.obsidian.getExplosionResistance(null));

			ICBMConfiguration.CONFIGURATION.save();

			OreDictionary.registerOre("dustSulfur", itemSulfurDust);
			OreGenerator.addOre(sulfureOreGenData);

			GameRegistry.registerTileEntity(TileMultiBlockPart.class, "TileEntityMultiBlockPart");

			isPreInit = true;
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if (!isInit)
		{
			isInit = true;
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if (!isPostInit)
		{
			/** LOAD. */

			// Sulfur
			GameRegistry.addSmelting(blockSulfurOre.blockID, new ItemStack(itemSulfurDust, 4), 0.8f);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 3), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', Item.coal }));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.gunpowder, 3), new Object[] { "@@@", "@?@", "@@@", '@', "dustSulfur", '?', new ItemStack(Item.coal, 1, 1) }));

			GameRegistry.addRecipe(new ShapedOreRecipe(Block.tnt, new Object[] { "@@@", "@R@", "@@@", '@', Item.gunpowder, 'R', Item.redstone }));

			// Poison Powder
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemPoisonPowder, 3), new Object[] { Item.spiderEye, Item.rottenFlesh }));

			isPostInit = true;
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