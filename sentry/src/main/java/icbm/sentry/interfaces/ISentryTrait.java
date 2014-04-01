package icbm.sentry.interfaces;

/** Advanced way to handle trait data vs just storing the trait as a single data value.
 * 
 * @author Darkguardsman */
public interface ISentryTrait
{
    /** Name of the trait */
    public String getName();

    /** Called when the sentry changes in a way that the trait needs to update */
    public void updateTrait(ITurret turret);

    /** Gets the value of the trait */
    public Object getValue();
}
