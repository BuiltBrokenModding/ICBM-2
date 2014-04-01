package icbm.sentry.turret.traits;

import icbm.sentry.interfaces.ISentryTrait;
import icbm.sentry.interfaces.ITurret;

/** Prefab for sentry traits
 * 
 * @author Darguardsman */
public abstract class SentryTrait implements ISentryTrait
{
    private String name;

    public SentryTrait(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void updateTrait(ITurret turret)
    {

    }
}
