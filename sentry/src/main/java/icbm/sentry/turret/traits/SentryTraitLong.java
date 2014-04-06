package icbm.sentry.turret.traits;

import icbm.sentry.interfaces.ITurret;

/** Trait used to define max energy storage
 * 
 * @author Darkguardsman */
public class SentryTraitLong extends SentryTraitUpgrade<Long>
{
    public SentryTraitLong(String name, String upgrade, long value)
    {
        super(name, upgrade, value);
    }

    @Override
    public void updateTrait(ITurret turret)
    {
        this.setValue((long) (getDefaultValue() + (getDefaultValue() * turret.getUpgradeEffect(getUpgradeName()))));
    }
}
