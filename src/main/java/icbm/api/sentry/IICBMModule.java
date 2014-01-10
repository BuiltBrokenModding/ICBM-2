package icbm.api.sentry;


/** Interface design to say this object is a peace that can be used to modify or construct a large
 * object.
 * 
 * @author DarkGuardsman */
public interface IICBMModule
{
    /** Called when the modular is update or first added to an object */
    public void init(IModuleContainer missile);

    /** Name of the part. */
    public String getName();

    /** Same as the ore directory for items. Will allow this to be used in place of other modulars */
    public String getOreName();

    /** Can this part be used inside this modular container */
    public boolean canBeUsedIn(IModuleContainer container);

    /** Called when the container updates with a new modular */
    public void update(IModuleContainer container);

    /** Called only once too see if this modular needs to update at all */
    public boolean shouldUpdate();

}
