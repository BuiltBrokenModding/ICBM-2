package icbm.sentry.turret.weapon;

import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

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
