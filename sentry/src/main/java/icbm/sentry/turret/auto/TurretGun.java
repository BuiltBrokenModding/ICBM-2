package icbm.sentry.turret.auto;

import icbm.Reference;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponConventional;
import universalelectricity.api.vector.Vector3;

/** @author DarkGuardsman */
public class TurretGun extends TurretAuto
{
    public TurretGun(TileTurret host)
    {
        super(host);
        weaponSystem = new WeaponConventional(this, 1, 10)
        {
            @Override
            public void fire(Vector3 target)
            {
                super.fire(target);
                world().playSoundEffect(x(), y(), z(), Reference.PREFIX + "machinegun", 5F, 1F - (world().rand.nextFloat() * 0.2f));
            }
        };
        centerOffset.y = 0.27;
        default_target_range = 40;
        maxCooldown = 30;
        barrelLength = 0.7f;
    }
}
