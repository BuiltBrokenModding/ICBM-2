package icbm.sentry.turret.weapon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.Reference;
import icbm.api.sentry.IAmmunition;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

/**
 * Any weapon that deals damage.
 * 
 * @author Calclavia
 */
public class WeaponDamage extends WeaponSystem
{
	protected float damage = 5f;
	protected DamageSource damageSource;

	public WeaponDamage(Turret sentry, DamageSource damageSource, float damage)
	{
		super(sentry);
		this.damage = damage;
		this.damageSource = damageSource;
	}

	@Override
	public void onHitEntity(Entity entity)
	{
		if (entity != null)
		{
			entity.attackEntityFrom(damageSource, damage);
		}
	}
}
