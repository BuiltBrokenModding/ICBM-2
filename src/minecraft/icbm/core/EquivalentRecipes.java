package icbm.core;

import ic2.api.Items;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Allows dynamic equivalent swapping of recipes. Looks for UE, then IC2 then vanilla equivalent
 * recipes.
 * 
 * @author Calclavia
 * 
 */
public class EquivalentRecipes
{
	/**
	 * Primary Metal: Steel
	 */
	public static String TIE_1 = ZhuYao.PREFIX + "tie1";
	public static String TIE_PIAN_1 = ZhuYao.PREFIX + "tiePian1";

	/**
	 * Secondary Metal: Bronze
	 */
	public static String TIE_2 = ZhuYao.PREFIX + "tie2";
	public static String TIE_PIAN_2 = ZhuYao.PREFIX + "tiePian2";

	/**
	 * Wire
	 */
	public static String DIAN_XIAN = ZhuYao.PREFIX + "dianXian";

	/**
	 * Battery
	 */
	public static String DIAN_CHI = ZhuYao.PREFIX + "dianChi";
	public static String DIAN_CHI_XIANG = ZhuYao.PREFIX + "dianChiXiang";

	/**
	 * Circuits
	 */
	public static String DIAN_LU_1 = ZhuYao.PREFIX + "dianLu1";
	public static String DIAN_LU_2 = ZhuYao.PREFIX + "dianLu2";
	public static String DIAN_LU_3 = ZhuYao.PREFIX + "dianLu3";
	/**
	 * Wrench
	 */
	public static String BA_SHOU = ZhuYao.PREFIX + "baShou";

	/**
	 * Called at mod init stage.
	 */
	public static void initiate()
	{
		/**
		 * Register materials.
		 */
		if (!registerItemStacksToDictionary(TIE_1, "ingotSteel"))
		{
			if (!registerItemStacksToDictionary(TIE_1, "ingotRefinedIron"))
			{
				registerItemStacksToDictionary(TIE_1, new ItemStack(Item.ingotIron));
			}
		}

		if (!registerItemStacksToDictionary(TIE_PIAN_1, "plateSteel"))
		{
			if (!registerItemStacksToDictionary(TIE_PIAN_1, Items.getItem("advancedAlloy")))
			{
				registerItemStacksToDictionary(TIE_PIAN_1, new ItemStack(Block.blockSteel));
			}
		}

		if (!registerItemStacksToDictionary(TIE_2, "ingotBronze"))
		{
			registerItemStacksToDictionary(TIE_2, new ItemStack(Item.brick));
		}

		if (!registerItemStacksToDictionary(TIE_PIAN_2, "plateBronze"))
		{
			if (!registerItemStacksToDictionary(TIE_PIAN_2, Items.getItem("carbonPlate")))
			{
				registerItemStacksToDictionary(TIE_PIAN_2, new ItemStack(Block.blockClay));
			}
		}

		if (!registerItemStacksToDictionary(DIAN_XIAN, "copperWire"))
		{
			if (!registerItemStacksToDictionary(DIAN_XIAN, Items.getItem("copperCableBlock")))
			{
				registerItemStacksToDictionary(DIAN_XIAN, new ItemStack(Item.redstone));
			}
		}

		if (!registerItemStacksToDictionary(DIAN_CHI, "battery"))
		{
			if (!registerItemStacksToDictionary(DIAN_CHI, Items.getItem("energyCrystal")))
			{
				registerItemStacksToDictionary(DIAN_CHI, new ItemStack(Block.blockRedstone));
			}
		}

		if (!registerItemStacksToDictionary(DIAN_CHI_XIANG, "batteryBox"))
		{
			if (!registerItemStacksToDictionary(DIAN_CHI_XIANG, Items.getItem("batBox")))
			{
				registerItemStacksToDictionary(DIAN_CHI_XIANG, new ItemStack(Block.blockGold));
			}
		}

		if (!registerItemStacksToDictionary(DIAN_LU_1, "basicCircuit"))
		{
			if (!registerItemStacksToDictionary(DIAN_LU_1, Items.getItem("electronicCircuit")))
			{
				registerItemStacksToDictionary(DIAN_LU_1, new ItemStack(Block.torchRedstoneIdle));
			}
		}

		if (!registerItemStacksToDictionary(DIAN_LU_2, "advancedCircuit"))
		{
			if (!registerItemStacksToDictionary(DIAN_LU_2, Items.getItem("advancedCircuit")))
			{
				registerItemStacksToDictionary(DIAN_LU_2, new ItemStack(Item.redstoneRepeater));
			}
		}

		if (!registerItemStacksToDictionary(DIAN_LU_3, "eliteCircuit"))
		{
			if (!registerItemStacksToDictionary(DIAN_LU_3, Items.getItem("iridiumPlate")))
			{
				registerItemStacksToDictionary(DIAN_LU_3, new ItemStack(Block.redstoneComparatorIdle));
			}
		}

		if (!registerItemStacksToDictionary(BA_SHOU, "wrench"))
		{
			if (!registerItemStacksToDictionary(BA_SHOU, Items.getItem("wrench")))
			{
				registerItemStacksToDictionary(BA_SHOU, new ItemStack(Item.axeSteel));
			}
		}
	}

	public static boolean registerItemStacksToDictionary(String name, List<ItemStack> itemStacks)
	{
		boolean returnValue = false;

		if (itemStacks.size() > 0)
		{
			for (ItemStack stack : itemStacks)
			{
				if (stack != null)
				{
					OreDictionary.registerOre(name, stack);
					returnValue = true;
				}
			}
		}

		return returnValue;
	}

	public static boolean registerItemStacksToDictionary(String name, ItemStack... itemStacks)
	{
		return registerItemStacksToDictionary(name, Arrays.asList(itemStacks));
	}

	public static boolean registerItemStacksToDictionary(String name, String stackName)
	{
		return registerItemStacksToDictionary(name, OreDictionary.getOres(stackName));
	}

}
