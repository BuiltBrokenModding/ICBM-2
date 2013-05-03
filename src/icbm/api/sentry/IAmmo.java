package icbm.api.sentry;

import net.minecraft.entity.Entity;

public interface IAmmo
{
	/**
	 * Gets the damage this ammo does to the target.
	 */
	public int getDamage(Entity entity);

	/**
	 * does damage to the target or creates a projectile
	 */
	public void attackTarget(Entity target);

	/**
	 * Type of projectile this is... Used to prevent some sentries from using it
	 */
	public ProjectileTypes getType();
	/**
	 * is the item consumed when shot
	 */
	public boolean consumeItem();
	
}
