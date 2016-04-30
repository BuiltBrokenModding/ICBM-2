package com.builtbroken.icbm.api.blast;

import com.builtbroken.icbm.api.missile.ITileMissile;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.explosive.IBlast;

/**
 * Applied to blasts that are static and remain in place doing a time duration effect. Keep in
 * mind this means the blast is no longer threaded normally. If the blast needs to be threaded
 * it must be handled by this blast on its own.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public interface IBlastTileMissile extends IBlast
{
    /**
     * Called each tick by the missile tile to process the blast
     *
     * @param tile
     * @param missile
     */
    void tickBlast(ITileMissile tile, IMissile missile);
}
