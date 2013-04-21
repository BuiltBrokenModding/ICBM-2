package mffs.api.security;

import java.util.Set;

import mffs.api.fortron.IFortronFrequency;
import mffs.api.modules.IModuleAcceptor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IDefenseStation extends IInventory, IFortronFrequency, IModuleAcceptor
{

	/**
	 * The range in which the defense station starts warning the player.
	 * 
	 * @return
	 */
	public int getWarningRange();

	/**
	 * The range in which the defense station has an effect on.
	 * 
	 * @return
	 */
	public int getActionRange();

	/**
	 * Merges an item into the defense station's safe keeping inventory.
	 * 
	 * @param itemStack
	 * @return True if kept, false if dropped.
	 */
	public boolean mergeIntoInventory(ItemStack itemStack);

	public Set<ItemStack> getFilteredItems();

	/**
	 * 
	 * @return True if the filtering is on ban mode. False if it is on allow-only mode.
	 */
	public boolean getFilterMode();

	public int getFortronCost();

	public IBiometricIdentifier getSecurityCenter();

	/**
	 * @return Is the defense station currently active?
	 */
	public boolean isActive();
}
