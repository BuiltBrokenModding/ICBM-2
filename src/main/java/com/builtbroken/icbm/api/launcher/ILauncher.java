package com.builtbroken.icbm.api.launcher;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.IWorldPosition;

/**
 * Applied to tiles that hold missiles for launching
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/28/2014.
 */
public interface ILauncher extends IWorldPosition
{
    /** Called to fire a missile at the pre-set target */
    boolean fireMissile();

    /** Called to fire a missile at the target */
    boolean fireMissile(IPos3D target);

    /**
     * Gets the missile in the launcher
     * @return missile
     */
    IMissile getMissile();
}
