package com.builtbroken.icbm.api.modules;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IRocketEngine
{
    /**
     * Grabs the speed that the rocket will fly at
     *
     * @return meters / tick
     */
    float getSpeed(IMissileModule missile);

    /**
     * Grabs the speed that the rocket will fly at
     *
     * @return distance in meters
     */
    float getMaxDistance(IMissileModule missile);
}
