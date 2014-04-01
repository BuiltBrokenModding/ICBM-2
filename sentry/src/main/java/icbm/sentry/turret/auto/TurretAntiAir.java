package icbm.sentry.turret.auto;

import icbm.Reference;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretUpgrade;
import icbm.sentry.turret.ai.TurretAntiAirSelector;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.traits.SentryTraitDouble;
import icbm.sentry.turret.traits.SentryTraitEnergy;
import icbm.sentry.turret.weapon.types.WeaponConventional;
import net.minecraft.entity.Entity;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.IVector3;

/** AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman */
public class TurretAntiAir extends TurretAuto
{
    public TurretAntiAir(TileTurret host)
    {
        super(host);
        this.weaponSystem = new WeaponConventional(this, 10);
        this.weaponSystem.soundEffect = Reference.PREFIX + "aagun";
        this.centerOffset.y = 0.75;
        newTrait(new SentryTraitDouble(ITurret.SEARCH_RANGE_TRAIT, ITurretUpgrade.TARGET_RANGE, 200.0));
        newTrait(new SentryTraitDouble(ITurret.MAX_HEALTH_TRAIT, 70.0));
        newTrait(new SentryTraitEnergy(10000));
        this.maxCooldown = 5;
        selector = new TurretAntiAirSelector(this);
    }

    @Override
    public void setTarget(Entity target)
    {
        super.setTarget(target);
        if (this.target != null)
        {
            this.getServo().setRotationSpeed(20);
        }
        else
        {
            this.getServo().setRotationSpeed(5);
        }
    }
}
