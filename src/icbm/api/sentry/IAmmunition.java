package icbm.api.sentry;

import net.minecraft.item.ItemStack;

/**
 * Applied to all TileEntities that can hold ammunition.
 * 
 * @author Calclavia
 * 
 */
public interface IAmmunition
{
	/**
	 * Checks if the platform has the specified ammunition.
	 * 
	 * @param itemStack
	 * @return
	 */
	public AmmoPair<IAmmo, ItemStack> hasAmmunition(ProjectileTypes ammunitionStack);

	/**
	 * Uses a specific type ammunition from the ammunition pool.
	 * 
	 * @param ammunitionStack - The type of ammunition to use.
	 * @return True if used successfully.
	 */
	public boolean useAmmunition(ItemStack ammunitionStack);

}
