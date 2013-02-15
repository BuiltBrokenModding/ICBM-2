package universalelectricity.prefab;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.electricity.ElectricInfo;
import universalelectricity.core.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.core.implement.IItemElectric;

/**
 * Extend from this class if your item requires electricity or to be charged. Optionally, you can
 * implement IItemElectric instead.
 * 
 * @author Calclavia
 * 
 */
public abstract class ItemElectric extends Item implements IItemElectric
{
	public ItemElectric(int id)
	{
		super(id);
		this.setMaxStackSize(1);
		this.setMaxDamage(100);
		this.setNoRepair();
	}

	/**
	 * Allows items to add custom lines of information to the mouseover description. If you want to
	 * add more information to your item, you can super.addInformation() to keep the electiricty
	 * info in the item info bar.
	 */
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		String color = "";
		double joules = this.getJoules(par1ItemStack);

		if (joules <= this.getMaxJoules(par1ItemStack) / 3)
		{
			color = "\u00a74";
		}
		else if (joules > this.getMaxJoules(par1ItemStack) * 2 / 3)
		{
			color = "\u00a72";
		}
		else
		{
			color = "\u00a76";
		}

		par3List.add(color + ElectricInfo.getDisplay(joules, ElectricUnit.JOULES) + " - " + Math.round((joules / this.getMaxJoules(par1ItemStack)) * 100) + "%");
	}

	/**
	 * Makes sure the item is uncharged when it is crafted and not charged. Change this if you do
	 * not want this to happen!
	 */
	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		par1ItemStack = this.getUncharged();
	}

	@Override
	public double onReceive(double amps, double voltage, ItemStack itemStack)
	{
		double rejectedElectricity = Math.max((this.getJoules(itemStack) + ElectricInfo.getJoules(amps, voltage, 1)) - this.getMaxJoules(itemStack), 0);
		this.setJoules(this.getJoules(itemStack) + ElectricInfo.getJoules(amps, voltage, 1) - rejectedElectricity, itemStack);
		return rejectedElectricity;
	}

	@Override
	public double onUse(double joulesNeeded, ItemStack itemStack)
	{
		double electricityToUse = Math.min(this.getJoules(itemStack), joulesNeeded);
		this.setJoules(this.getJoules(itemStack) - electricityToUse, itemStack);
		return electricityToUse;
	}

	@Override
	public boolean canReceiveElectricity()
	{
		return true;
	}

	@Override
	public boolean canProduceElectricity()
	{
		return false;
	}

	/**
	 * This function sets the electriicty. Do not directly call this function. Try to use
	 * onReceiveElectricity or onUseElectricity instead.
	 * 
	 * @param wattHours - The amount of electricity in joules
	 */
	@Override
	public void setJoules(double wattHours, Object... data)
	{
		if (data[0] instanceof ItemStack)
		{
			ItemStack itemStack = (ItemStack) data[0];

			// Saves the frequency in the ItemStack
			if (itemStack.getTagCompound() == null)
			{
				itemStack.setTagCompound(new NBTTagCompound());
			}

			double electricityStored = Math.max(Math.min(wattHours, this.getMaxJoules(itemStack)), 0);
			itemStack.getTagCompound().setDouble("electricity", electricityStored);

			/**
			 * Sets the damage as a percentage to render the bar properly.
			 */
			itemStack.setItemDamage((int) (100 - (electricityStored / getMaxJoules()) * 100));
		}
	}

	/**
	 * This function is called to get the electricity stored in this item
	 * 
	 * @return - The amount of electricity stored in watts
	 */
	@Override
	public double getJoules(Object... data)
	{
		if (data[0] instanceof ItemStack)
		{
			ItemStack itemStack = (ItemStack) data[0];

			if (itemStack.getTagCompound() == null)
			{
				return 0;
			}

			double electricityStored = itemStack.getTagCompound().getDouble("electricity");

			/**
			 * Sets the damage as a percentage to render the bar properly.
			 */
			itemStack.setItemDamage((int) (100 - (electricityStored / getMaxJoules()) * 100));
			return electricityStored;
		}

		return -1;
	}

	/**
	 * Returns an uncharged version of the electric item. Use this if you want the crafting recipe
	 * to use a charged version of the electric item instead of an empty version of the electric
	 * item
	 * 
	 * @return The ItemStack of a fully charged electric item
	 */
	public ItemStack getUncharged()
	{
		return this.getWithCharge(0);
	}

	public ItemStack getWithCharge(double joules)
	{
		ItemStack chargedItem = new ItemStack(this);
		((IItemElectric) chargedItem.getItem()).setJoules(joules, chargedItem);
		return chargedItem;
	}

	public static ItemStack getWithCharge(ItemStack itemStack, double joules)
	{
		if (itemStack.getItem() instanceof IItemElectric)
		{
			ItemStack chargedItem = itemStack.copy();
			((IItemElectric) chargedItem.getItem()).setJoules(joules, chargedItem);
			return chargedItem;
		}

		return null;
	}

	public static ItemStack getUncharged(ItemStack itemStack)
	{
		return getWithCharge(itemStack, 0);
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		// Add an uncharged version of the electric item
		par3List.add(this.getUncharged());
		// Add an electric item to the creative list that is fully charged
		ItemStack chargedItem = new ItemStack(this);
		par3List.add(this.getWithCharge(this.getMaxJoules(chargedItem)));
	}
}
