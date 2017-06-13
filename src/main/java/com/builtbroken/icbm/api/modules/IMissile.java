package com.builtbroken.icbm.api.modules;

import com.builtbroken.icbm.content.missile.parts.casing.MissileSize;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleContainer;

/**
 * Object module for the missile that contains parts... etc
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public interface IMissile extends IModuleContainer, IModule
{
    /**
     * Size of the missile @see {@link MissileSize}
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

    void setWarhead(IWarhead warhead);

    void setGuidance(IGuidance guidance);

    void setEngine(IRocketEngine engine);

    IWarhead getWarhead();

    IGuidance getGuidance();

    IRocketEngine getEngine();

    /**
     * Max number of hit points the missile has
     *
     * @return
     */
    default float getMaxHitPoints()
    {
        return 15;
    }

    /**
     * How tall is the missile, mainly used
     * for collsion box code and rendering
     *
     * @return height in meters
     */
    default double getHeight()
    {
        return 1;
    }

    /**
     * How wide is the missile, mainly used
     * for collsion box code and rendering
     *
     * @return height in meters
     */
    default double getWidth()
    {
        return 1;
    }
}
