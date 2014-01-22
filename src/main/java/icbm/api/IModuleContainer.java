package icbm.api;

import icbm.api.sentry.IICBMModule;

import java.util.List;

/** Interface designed to be used in cases were an object contains several modular parts to create
 * its over all design
 * 
 * @author DarkGuardsman */
public interface IModuleContainer
{
    /** Set of modular inside this modular container */
    public List<IICBMModule> getModulars();

    public boolean canAddModular(IICBMModule modular);
}
