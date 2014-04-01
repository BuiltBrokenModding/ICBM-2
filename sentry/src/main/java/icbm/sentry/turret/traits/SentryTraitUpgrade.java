package icbm.sentry.turret.traits;

/** Sentry Trait that can be upgraded
 * 
 * @author DarkGuardsman */
public abstract class SentryTraitUpgrade<O> extends SentryTrait<O>
{

    private String upgrade_name;

    public SentryTraitUpgrade(String name)
    {
        super(name);
    }

    public SentryTraitUpgrade(String name, O default_value)
    {
        super(name, default_value);
    }

    public SentryTraitUpgrade(String name, String upgrade_name, O default_value)
    {
        super(name, default_value);
        this.upgrade_name = upgrade_name;
    }

    /** Sets the upgrade name */
    public void setUpgradeName(String name)
    {
        this.upgrade_name = name;
    }

    /** Gets the name of the upgrade */
    public String getUpgradeName()
    {
        return upgrade_name;
    }
}
