package icbm.api.sentry;

import universalelectricity.core.vector.Vector3;
import dark.library.helpers.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IAmmo
{
	/**
	 * Gets the damage this ammo does to the target. Only used if applyDirectDamage returns true
	 */
	public Pair<DamageSource, Integer> getDamage(Entity entity, int meta);

	/**
	 * Should the damage be done directly instead of creating a projectile or calling fire classes
	 * 
	 * @return
	 */
	public boolean applyDirectDamage(int meta);

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

}
