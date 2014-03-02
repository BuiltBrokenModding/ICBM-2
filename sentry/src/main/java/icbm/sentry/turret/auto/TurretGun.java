package icbm.sentry.turret.auto;

import universalelectricity.api.vector.Vector3;
import icbm.Reference;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponProjectile;

/** @author DarkGuardsman */
public class TurretGun extends TurretAuto
{
	public TurretGun(TileTurret host)
	{
		super(host);
		weaponSystem = new WeaponProjectile(this, 10)
		{
			@Override
			public void fire(Vector3 target)
			{
				super.fire(target);
				turret.getHost().world().playSoundEffect(turret.getHost().x(), turret.getHost().y(), turret.getHost().z(), Reference.PREFIX + "machinegun", 5F, 1F - (turret.getHost().world().rand.nextFloat() * 0.2f));
			}
		};
		centerOffset.y = 0.27;
		range = 20;
		maxCooldown = 30;
		barrelLength = 0.7f;
	}
}
