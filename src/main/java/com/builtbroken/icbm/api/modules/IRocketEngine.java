package com.builtbroken.icbm.api.modules;

import com.builtbroken.icbm.api.missile.IMissileEntity;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IRocketEngine extends IMissileModule
{
    /**
     * Grabs the speed that the rocket will fly at
     *
     * @param missile - module containing this engine
     * @return meters / tick
     */
    float getSpeed(IMissile missile);

    /**
     * Grabs the speed that the rocket will fly at
     *
     * @param missile - module containing this engine
     * @return distance in meters
     */
    float getMaxDistance(IMissile missile);

    /**
     * Called when a missile is fired
     *
     * @param missile       - entity missile
     * @param missileModule - module missile
     */
    void onLaunch(IMissileEntity missile, IMissile missileModule);

    /**
     * Called when the missile was destroyed. Take this time
     * to ignite remain fuel in an explosive way.
     *
     * @param missile       - entity missile
     * @param missileModule - module missile
     */
    void onDestroyed(IMissileEntity missile, IMissile missileModule);
}
