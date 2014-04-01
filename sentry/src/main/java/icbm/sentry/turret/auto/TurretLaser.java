package icbm.sentry.turret.auto;

import icbm.Reference;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretUpgrade;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.traits.SentryTraitDouble;
import icbm.sentry.turret.traits.SentryTraitEnergy;
import icbm.sentry.turret.weapon.types.WeaponTwinLaser;
import net.minecraft.util.MathHelper;

public class TurretLaser extends TurretAuto
{
    /** Laser turret spins its barrels every shot. */
    public float barrelRotation;
    public float barrelRotationVelocity;

    public TurretLaser(TileTurret host)
    {
        super(host);
        weaponSystem = new WeaponTwinLaser(this, 2, 100000);
        weaponSystem.soundEffect = Reference.PREFIX + "lasershot";
        newTrait(new SentryTraitDouble(ITurret.SEARCH_RANGE_TRAIT, ITurretUpgrade.TARGET_RANGE, 15.0));
        newTrait(new SentryTraitDouble(ITurret.MAX_HEALTH_TRAIT, 50.0));
        newTrait(new SentryTraitEnergy(1000000));
        barrelLength = 1.2f;
        maxCooldown = 15;
    }

    @Override
    public void update()
    {
        super.update();

        if (this.world().isRemote)
        {
            this.barrelRotation = MathHelper.wrapAngleTo180_float(this.barrelRotation + this.barrelRotationVelocity);
            this.barrelRotationVelocity = Math.max(this.barrelRotationVelocity - 0.1f, 0);
        }
    }
}
