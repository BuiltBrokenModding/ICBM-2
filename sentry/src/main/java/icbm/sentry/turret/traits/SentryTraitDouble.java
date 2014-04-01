package icbm.sentry.turret.traits;

import icbm.sentry.interfaces.ITurret;

/** @author Darkguardsman */
public class SentryTraitDouble extends SentryTrait
{
    private double value;
    private final double default_value;
    private String upgrade_name;

    public SentryTraitDouble(String name, double value)
    {
        super(name);
        this.value = value;
        this.default_value = value;
    }

    public SentryTraitDouble(String name, String upgrade, double value)
    {
        this(name, value);
        this.upgrade_name = upgrade;
    }

    @Override
    public void updateTrait(ITurret turret)
    {
        if (this.upgrade_name != null)
            this.value = default_value + (default_value * turret.getUpgradeEffect(this.upgrade_name));
    }

    @Override
    public Object getValue()
    {
        return value;
    }

}
