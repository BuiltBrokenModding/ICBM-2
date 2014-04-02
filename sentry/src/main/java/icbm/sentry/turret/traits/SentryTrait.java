package icbm.sentry.turret.traits;

import icbm.sentry.interfaces.ISentryTrait;

/** Prefab for sentry traits
 * 
 * @author Darguardsman
 * @param <O> */
public abstract class SentryTrait<O extends Object> implements ISentryTrait<O>
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

    @Override
    public String toString()
    {
        return "[SentryTrait]Name: " + getName() + " Value: " + getValue() + " Default: " + getDefaultValue();
    }

    public static int asInt(ISentryTrait trait)
    {
        return asInt(trait, 0);
    }

    public static int asInt(ISentryTrait<? extends Object> trait, int d)
    {
        if (trait != null)
            if (trait.getValue() instanceof Integer || trait.getValue() instanceof Double || trait.getValue() instanceof Long)
            {
                return (Integer) trait.getValue();
            }
        return d;
    }

    public static double asDouble(ISentryTrait trait)
    {
        return asInt(trait, 0);
    }

    public static double asDouble(ISentryTrait<? extends Object> trait, double d)
    {
        if (trait != null)
            if (trait.getValue() instanceof Integer || trait.getValue() instanceof Double || trait.getValue() instanceof Long)
            {
                return (Double) trait.getValue();
            }
        return d;
    }

    public static long asLong(ISentryTrait<? extends Object> trait)
    {
        return asLong(trait, 0L);
    }

    public static long asLong(ISentryTrait<? extends Object> trait, long d)
    {
        if (trait != null)
            if (trait.getValue() instanceof Integer || trait.getValue() instanceof Double || trait.getValue() instanceof Long)
            {
                return (Long) trait.getValue();
            }
        return d;
    }
}
