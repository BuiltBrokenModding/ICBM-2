package com.builtbroken.icbm.api.modules;

import com.builtbroken.icbm.api.missile.IMissileEntity;

import java.awt.*;

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
     * @return true if the engine exploded
     */
    boolean onDestroyed(IMissileEntity missile, IMissile missileModule);

    /**
     * Called to see if the engine creates fire particles, trail, or blocks
     * when running. This is used to set the ground on fire if the engine
     * is pointed at the ground. As well catch entities on fire behind
     * the missile.
     *
     * @param missile       - missile entity, may be null if item is being checked
     * @param missileModule - missile module, may be null in rare cases
     * @return true if the engine generates fire
     */
    boolean generatesFire(IMissileEntity missile, IMissile missileModule);

    /**
     * Gets the color of an engine
     *
     * @param missile       - may be null
     * @param missileModule - may be null
     * @return color, or null to disable
     */
    Color getEngineFireColor(IMissileEntity missile, IMissile missileModule);

    /**
     * Gets the color of an engine smoke trail
     *
     * @param missile       - may be null
     * @param missileModule - may be null
     * @return color, or null to disable
     */
    Color getEngineSmokeColor(IMissileEntity missile, IMissile missileModule);
}
