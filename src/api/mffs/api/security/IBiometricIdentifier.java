package mffs.api.security;

import net.minecraft.item.ItemStack;

/**
 * Applied to Biometric Identifiers (extends TileEntity).
 */
public interface IBiometricIdentifier
{
	/**
	 * Is access granted to this specific user?
	 * 
	 * @param username - Minecraft username.
	 * @param permission - The permission.
	 * @return
	 */
	public boolean isAccessGranted(String username, Permission permission);

	/**
	 * Gets the owner of the security center.
	 * 
	 * @return
	 */
	public String getOwner();

	/**
	 * Gets the card currently placed in the manipulating slot.
	 * 
	 * @return
	 */
	public ItemStack getManipulatingCard();
}
