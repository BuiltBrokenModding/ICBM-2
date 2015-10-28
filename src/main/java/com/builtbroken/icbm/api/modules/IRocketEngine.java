package com.builtbroken.icbm.api.modules;

import com.builtbroken.icbm.api.IMissile;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IRocketEngine
{
    /**
     * Grabs the speed that the rocket will fly at
     *
     * @param missile - module containing this engine
     * @return meters / tick
     */
    float getSpeed(IMissileModule missile);

    /**
     * Grabs the speed that the rocket will fly at
     *
     * @param missile - module containing this engine
     * @return distance in meters
     */
    float getMaxDistance(IMissileModule missile);

    /**
     * Called when a missile is fired
     *
     * @param missile       - entity missile
     * @param missileModule - module missile
     */
    void onLaunch(IMissile missile, IMissileModule missileModule);

    /**
     * Called when the missile was destroyed. Take this time
     * to ignite remain fuel in an explosive way.
     *
     * @param missile       - entity missile
     * @param missileModule - module missile
     */
    void onDestroyed(IMissile missile, IMissileModule missileModule);
}
