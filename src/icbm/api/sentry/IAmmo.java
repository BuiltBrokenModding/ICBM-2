package icbm.api.sentry;

import icbm.core.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public interface IAmmo
{
	/**
	 * Gets the damage this ammo does to the target. Only used if applyDirectDamage returns true
	 */
	public Pair<DamageSource, Integer> getDamage(Entity entity, Object shooter, int meta);

	/**
	 * Should the damage be done directly instead of creating a projectile or calling fire classes
	 * 
	 * @return
	 */
	public boolean isDirectDamage(ItemStack itemStack);

	/**
	 * Fires the ammo at a target same as location but this can be used to get the center mass of a
	 * target for moving projectiles. As well to auto tracking projectiles
	 * 
	 * @param target - instanceof Entity
	 * @param meta- meta of the ammo item
	 * @return
	 */
	public boolean fireAmmoLiving(Entity target, int meta);

	/**
	 * Fires the ammo at a location useful for dummy fire weapons
	 * 
	 * @param world
	 * @param target
	 * @param meta - meta of the ammo item
	 * @return true if was fired
	 */
	public boolean fireAmmoLoc(World world, Vector3 target, int meta);

	/**
	 * Type of projectile this is... Used to prevent some sentries from using it
	 */
	public ProjectileTypes getType(int meta);

	/**
	 * Used to either consume the item or damage the item after its been fired
	 */
	public ItemStack consumeItem(ItemStack itemStack);

	/**
	 * Called when an ammo container goes to drop this item into the world. Useful to prevent inf
	 * ammo from being dropped
	 */
	public boolean canDrop(int meta);

	/**
	 * Called when the item is added to the world when a ammo container is forcefully broken. Useful
	 * it the ammo should detonate or cause extra havoc
	 * 
	 * @return what is left of the stack after action. Return intire stack if nothing happens
	 */
	public ItemStack onDroppedIntoWorld(ItemStack stack);

}
