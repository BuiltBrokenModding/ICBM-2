package mffs.api.security;

import java.util.Set;

import mffs.api.IActivatable;
import mffs.api.IBiometricIdentifierLink;
import mffs.api.fortron.IFortronFrequency;
import mffs.api.modules.IModuleAcceptor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IInterdictionMatrix extends IInventory, IFortronFrequency, IModuleAcceptor, IBiometricIdentifierLink, IActivatable
{

	/**
	 * The range in which the Interdiction Matrix starts warning the player.
	 * 
	 * @return
	 */
	public int getWarningRange();

	/**
	 * The range in which the Interdiction Matrix has an effect on.
	 * 
	 * @return
	 */
	public int getActionRange();

	/**
	 * Merges an item into the Interdiction Matrix's safe keeping inventory.
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

	@Override
	public int getFortronCost();
}
