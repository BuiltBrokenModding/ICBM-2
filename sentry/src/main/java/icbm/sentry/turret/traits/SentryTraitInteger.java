package icbm.sentry.turret.traits;

import icbm.sentry.interfaces.ITurret;

/** @author Darkguardsman */
public class SentryTraitInteger extends SentryTraitUpgrade<Integer>
{
    public SentryTraitInteger(String name, int value)
    {
        super(name, value);
    }

    public SentryTraitInteger(String name, String upgrade, int value)
    {
        this(name, value);
    }

    @Override
    public void updateTrait(ITurret turret)
    {
        if (getUpgradeName() != null)
            setValue((int) (getDefaultValue() + (getDefaultValue() * turret.getUpgradeEffect(getUpgradeName()))));
    }
}
