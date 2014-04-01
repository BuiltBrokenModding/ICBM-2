package icbm.sentry.turret.traits;

import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretUpgrade;

/** Trait used to define max energy storage
 * 
 * @author Darkguardsman */
public class SentryTraitEnergy extends SentryTraitUpgrade<Long>
{
    public SentryTraitEnergy(long value)
    {
        super(ITurret.ENERGY_STORAGE_TRAIT, ITurretUpgrade.ENERGY_UPGRADE, value);
    }

    @Override
    public void updateTrait(ITurret turret)
    {
        this.setValue((long) (getDefaultValue() + (getDefaultValue() * turret.getUpgradeEffect(getUpgradeName()))));
    }
}
