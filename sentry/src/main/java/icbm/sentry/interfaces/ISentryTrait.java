package icbm.sentry.interfaces;

/** Advanced way to handle trait data vs just storing the trait as a single data value.
 * 
 * @author Darkguardsman */
public interface ISentryTrait<O>
{
    /** Name of the trait */
    public String getName();

    /** Called when the sentry changes in a way that the trait needs to update */
    public void updateTrait(ITurret turret);

    /** Gets the value of the trait */
    public O getValue();

    /** Sets the current value overriding the auto update value */
    public void setValue(O obj);

    /** Gets the defaut value of the trait */
    public O getDefaultValue();

    /** Sets the default value */
    public void setDefaultValue(O obj);
}
