package com.builtbroken.icbm.api.warhead;

import com.builtbroken.mc.api.modules.IModule;

/** Module that acts as a trigger
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/21/2016.
 */
public interface ITrigger extends IModule
{
    /**
     * Called when the trigger is installed
     * @param module
     */
    void addedToDevice(IModule module);

    /**
     * Called when the trigger is removed
     * @param module
     */
    void removedFromDevice(IModule module);
}
