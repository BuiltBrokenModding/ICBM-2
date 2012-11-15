package atomicscience.api;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.ItemStack;

/**
 * Apply this to all item armors and it will
 * prevent the player from receiving a specific
 * type of poison.
 */
public interface IAntiPoisonArmor
{
	/**
	 * @itemStack - The item stack this armor is
	 *            in.
	 * @type - The type of poison given to this
	 *       entity
	 * @return - Returns true if this armor
	 *         prevents poison from the player.
	 */
	public boolean isProtectedFromPoison(ItemStack itemStack, EntityLiving entityLiving, Poison type);

	/**
	 * Called when this piece of armor protects a
	 * player from a specific poison.
	 * 
	 * @itemStack - The item stack this armor is
	 *            in.
	 * @type - The type of poison given to this
	 *       entity
	 */
	public void onProtectFromPoison(ItemStack itemStack, EntityLiving entityLiving, Poison type);
}
