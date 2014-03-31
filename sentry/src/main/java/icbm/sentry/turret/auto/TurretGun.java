package icbm.sentry.turret.auto;

import icbm.Reference;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.types.WeaponConventional;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.IVector3;

/** @author DarkGuardsman */
public class TurretGun extends TurretAuto
{
    public TurretGun(TileTurret host)
    {
        super(host);
		weaponSystem = new WeaponConventional(this, 1, 8)
        {
            @Override
            public void fire(IVector3 target)
            {
                super.fire(target);
                world().playSoundEffect(x(), y(), z(), Reference.PREFIX + "machinegun", 5F, 1F - (world().rand.nextFloat() * 0.2f));
            }
        };
		centerOffset.y = 0.3;
		aimOffset.y = 0.3;
        applyTrait(ITurret.SEARCH_RANGE_TRAIT, 20.0);
        applyTrait(ITurret.MAX_HEALTH_TRAIT, 50.0);
        maxCooldown = 10;
        barrelLength = 1f;
    }
}
