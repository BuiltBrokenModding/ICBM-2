package icbm.api.sentry;

import icbm.gangshao.turret.TileEntityTurretBase;
import net.minecraft.entity.Entity;

public interface IAmmo
{
	/**
	 * Gets the damage this ammo does to the target.
	 */
	public int getDamage(int meta, Entity entity);

	/**
	 * does damage to the target or creates a projectile if target is null this means the round was
	 * fire at a location or impacted the location
	 */
	public void attackTargetLiving(int meta, TileEntityTurretBase turret, Entity target, boolean hit);

	/**
	 * Type of projectile this is... Used to prevent some sentries from using it
	 */
	public ProjectileTypes getType(int meta);

	/**
	 * is the item consumed when shot
	 */
	public boolean consumeItem(int meta);

}
