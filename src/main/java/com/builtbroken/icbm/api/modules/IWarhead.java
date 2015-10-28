package com.builtbroken.icbm.api.modules;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHolder;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IWarhead extends IModule, IExplosiveHolder
{
    /**
     * Called to trigger the explosive in the warhead. Should just call the internal explosive code or explosive handle.
     *
     * @param triggerCause - what caused the explosive to go off
     * @param world        - current world the explosion will go off in
     * @param x            - location x
     * @param y            - location y
     * @param z            - location z
     * @return the result of the explosion, see enum for details
     */
    WorldChangeHelper.ChangeResult trigger(TriggerCause triggerCause, World world, double x, double y, double z);
}
