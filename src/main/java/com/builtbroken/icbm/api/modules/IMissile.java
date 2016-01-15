package com.builtbroken.icbm.api.modules;

import com.builtbroken.mc.api.modules.IModuleContainer;

/**
 * Object module for the missile that contains parts... etc
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IMissile extends IModuleContainer
{
    /**
     * Size of the missile @see {@link com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings}
     *
     * @return size of the missile
     */
    int getMissileSize();

    /**
     * Checks if the missile can launch from a silo. Checks for
     * engine installed, engine fuel, and guidance chip.
     *
     * @return true if it can launch.
     */
    boolean canLaunch();
}
