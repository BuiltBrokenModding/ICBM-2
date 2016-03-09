package com.builtbroken.icbm.api.missile;

/**
 * Applied to entities to get a FoF tag. Used by AMS to not shoot down friendly missiles.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2016.
 */
public interface IFoF
{
    /**
     * Gets FoF tag used to ID this missile
     *
     * @return valid string
     */
    String getFoFTag();
}
