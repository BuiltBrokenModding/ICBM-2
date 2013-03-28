package mffs.api.fortron;

import net.minecraft.item.ItemStack;

public abstract interface IItemFortronStorage
{
	/**
	 * Sets the amount of fortron energy.
	 * 
	 * @param joules
	 */
	public void setFortronEnergy(int joules, ItemStack itemStack);

	/**
	 * @return Gets the amount of fortron stored.
	 */
	public int getFortronEnergy(ItemStack itemStack);

	/**
	 * 
	 * @return Gets the maximum possible amount of fortron that can be stored.
	 */
	public int getFortronCapacity(ItemStack itemStack);
}