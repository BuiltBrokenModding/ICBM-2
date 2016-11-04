package com.builtbroken.icbm.content.rail.powered;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/4/2016.
 */
public enum PoweredRails
{
    ROTATION,
    POWERED,
    STOP,
    ORIENTATION,
    LOADER,
    UNLOADER,
    EXTENDER,
    SPLITTER;

    public static PoweredRails get(int railType)
    {
        //TODO add error handling
        if(railType >= 0 && railType < values().length)
        {
            return values()[railType];
        }
        return POWERED;
    }
}
