package icbm.sentry.turret.traits;

import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretUpgrade;

public class SentryTraitEnergy extends SentryTrait
{
    private long value;
    private final long default_value;

    public SentryTraitEnergy(long value)
    {
        super(ITurret.ENERGY_STORAGE_TRAIT);
        this.value = value;
        this.default_value = value;
    }

    @Override
    public void updateTrait(ITurret turret)
    {
        this.value = (long) (default_value + (default_value * turret.getUpgradeEffect(ITurretUpgrade.ENERGY_UPGRADE)));
    }

    @Override
    public Object getValue()
    {
        return value;
    }

}
