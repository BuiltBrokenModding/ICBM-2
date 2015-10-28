package com.builtbroken.icbm.api.modules;

import com.builtbroken.mc.api.modules.IModuleContainer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IMissileModule extends IModuleContainer
{
    /**
     * Size of the missile @see {@link com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings}
     *
     * @return size of the missile
     */
    int getMissileSize();
}
