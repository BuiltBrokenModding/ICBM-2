package com.builtbroken.icbm.api.launcher;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/22/2016.
 */
public interface INamedLauncher extends ILauncher
{
    /**
     * Name of the launcher
     *
     * @return name, or null if it doesn't have a name
     */
    String getLauncherName();
}
