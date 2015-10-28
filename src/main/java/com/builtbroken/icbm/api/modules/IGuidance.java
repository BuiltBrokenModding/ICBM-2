package com.builtbroken.icbm.api.modules;

import com.builtbroken.mc.api.modules.IModule;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IGuidance extends IModule
{
    /**
     * How far from the target can the missile land.
     *
     * @param missile - missile, may be null
     * @return distance, greater than zero
     */
    float getFallOffRange(IMissileModule missile);

    /**
     * Chance the guidance can fail landing so far away from
     * the original target. Eg random location along the path,
     * or strait into the ground.
     *
     * @param missile - missile, may be null
     * @return change between 0f-1f
     */
    float getChanceToFail(IMissileModule missile);
}
