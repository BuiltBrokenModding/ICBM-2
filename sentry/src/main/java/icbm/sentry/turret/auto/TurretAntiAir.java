package icbm.sentry.turret.auto;

import icbm.api.sentry.IAATarget;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.item.ItemStack;
import universalelectricity.api.vector.Vector3;
import icbm.Reference;
import icbm.api.sentry.IAmmunition;
import icbm.api.sentry.ProjectileType;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.items.ItemAmmo.AmmoType;
import icbm.sentry.turret.weapon.WeaponProjectile;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 */
public class TurretAntiAir extends TurretAuto
{
	public TurretAntiAir(TileTurret host)
	{
		super(host);
		this.weaponSystem = new WeaponProjectile(this, 1, 10)
		{
			@Override
			public boolean isAmmo(ItemStack stack)
			{
				return super.isAmmo(stack) && ((IAmmunition) stack.getItem()).getType(stack) == ProjectileType.CONVENTIONAL;
			}

			@Override
			public void fire(Vector3 target)
			{
				super.fire(target);
				turret.getHost().world().playSoundEffect(turret.getHost().x(), turret.getHost().y(), turret.getHost().z(), Reference.PREFIX + "aagun", 5F, 1F - (turret.getHost().world().rand.nextFloat() * 0.2f));
			}
		};
		this.centerOffset.y = 0.75;
	}

    @Override
    public boolean canFire ()
    {
        if (this.target instanceof IAATarget || this.target instanceof EntityWither || this.target instanceof EntityBat)
            return super.canFire();
        return false;
    }
}
