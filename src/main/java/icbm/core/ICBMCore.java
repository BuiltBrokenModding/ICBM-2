package icbm.core;

import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;

import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import calclavia.lib.multiblock.link.BlockMulti;
import calclavia.lib.multiblock.link.TileMultiBlockPart;
import calclavia.lib.network.PacketPlayerItem;
import calclavia.lib.network.PacketTile;
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
@ModstatInfo(prefix = "icbm", name = Reference.NAME, version = Reference.VERSION)
public class ICBMCore
{
	public static final ICBMCore INSTANCE = new ICBMCore();

	public static Block blockMulti, blockSulfurOre, blockRadioactive;

	public static Item itemSulfurDust, itemPoisonPowder;

	public static OreGenBase sulfureOreGenData;

	private static boolean isPreInit, isInit, isPostInit;

	public static final Logger LOGGER = Logger.getLogger(Reference.NAME);

	public static final PacketTile PACKET_TILE = new PacketTile(Reference.CHANNEL);
	public static final PacketPlayerItem PACKET_ITEM = new PacketPlayerItem(Reference.CHANNEL);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		if (!isPreInit)
		{
			Modstats.instance().getReporter().registerMod(INSTANCE);
			MinecraftForge.EVENT_BUS.register(INSTANCE);

			LOGGER.fine("Loaded " + LanguageUtility.loadLanguages(icbm.Reference.LANGUAGE_PATH, icbm.Reference.LANGUAGES) + " languages.");

			Settings.initiate();
			Settings.CONFIGURATION.load();

			// BLOCKS
			blockSulfurOre = new BlockSulfureOre(Settings.getNextBlockID());
			blockMulti = new BlockMulti(Settings.getNextBlockID()).setPacketType(PACKET_TILE);

			// Items
			itemPoisonPowder = new ItemICBMBase(Settings.getNextItemID(), "poisonPowder");
			itemSulfurDust = new ItemICBMBase(Settings.getNextItemID(), "sulfur");

			// -- Registering Blocks
			GameRegistry.registerBlock(blockSulfurOre, "blockSulferOre");

			sulfureOreGenData = new OreGeneratorICBM("Sulfur Ore", "oreSulfur", new ItemStack(blockSulfurOre), 0, 40, 20, 4).enable(Settings.CONFIGURATION);

			/** Check for existence of radioactive block. If it does not exist, then create it. */
			if (OreDictionary.getOres("blockRadioactive").size() > 0)
			{
				blockRadioactive = Block.blocksList[OreDictionary.getOres("blockRadioactive").get(0).itemID];
				LOGGER.fine("Detected radioative block from another mod, utilizing it.");
			}

			/** Decrease Obsidian Resistance */
			Block.obsidian.setResistance(Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45).getInt(45));
			LOGGER.fine("Changed obsidian explosive resistance to: " + Block.obsidian.getExplosionResistance(null));

			Settings.CONFIGURATION.save();

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

	public static void setModMetadata(String id, ModMetadata metadata)
	{
		metadata.modId = id;
		metadata.name = Reference.NAME;
		metadata.description = "ICBM is a Minecraft Mod that introduces intercontinental ballistic missiles to Minecraft. But the fun doesn't end there! This mod also features many different explosives, missiles and machines classified in three different tiers. If strategic warfare, carefully coordinated airstrikes, messing with matter and general destruction are up your alley, then this mod is for you!";
		metadata.url = "http://www.calclavia.com/icbm/";
		metadata.logoFile = "/icbm_logo.png";
		metadata.version = Reference.VERSION;
		metadata.authorList = Arrays.asList(new String[] { "Calclavia" });
		metadata.credits = "Please visit the website.";
		metadata.autogenerated = false;
	}

	protected String getChannel()
	{
		return null;
	}
}