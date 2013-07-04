package icbm.gangshao;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IAmmunition
{

	/**
	 * Fires the ammo at a target same as location but this can be used to get the center mass of a
	 * target for moving projectiles. As well to auto tracking projectiles.
	 * 
	 * @param target - Entity target
	 * @param itemStack - ItemStack
	 * @return
	 */
	public void onFire(Entity target, ItemStack itemStack);

	/**
	 * Called when an ammo container goes to drop this item into the world. Useful to prevent
	 * infinite ammo from being dropped
	 */
	public boolean canDrop(int meta);

	/**
	 * Called when the item is added to the world when a ammo container is forcefully broken. Useful
	 * it the ammo should detonate or cause extra havoc
	 * 
	 * @return what is left of the stack after action. Return intire stack if nothing happens
	 */
	public ItemStack onDroppedIntoWorld(ItemStack stack);

	public ProjectileType getType(int meta);

}
