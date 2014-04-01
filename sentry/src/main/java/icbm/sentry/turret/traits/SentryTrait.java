package icbm.sentry.turret.traits;

import icbm.sentry.interfaces.ISentryTrait;
import icbm.sentry.interfaces.ITurret;

/** Prefab for sentry traits
 * 
 * @author Darguardsman
 * @param <O> */
public abstract class SentryTrait<O> implements ISentryTrait<O>
{
    private final String name;
    private O value;
    private O default_value;

    public SentryTrait(String name)
    {
        this.name = name;
    }

    public SentryTrait(String name, O default_value)
    {
        this(name);
        this.default_value = default_value;
        this.value = this.default_value;
    }

    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public O getValue()
    {
        return value;
    }

    @Override
    public void setValue(O obj)
    {
        value = obj;
    }

    @Override
    public O getDefaultValue()
    {
        return default_value;
    }

    @Override
    public void setDefaultValue(O obj)
    {
        default_value = obj;
    }
}
