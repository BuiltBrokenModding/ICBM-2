package icbm.explosion.machines;

import java.util.HashSet;

import universalelectricity.api.vector.Vector2;

public class MissileLauncherRegistry
{
    private static HashSet<TileLauncherPrefab> launcherList = new HashSet<TileLauncherPrefab>();

    public static void registerLauncher(TileLauncherPrefab launcher)
    {
        if (!launcher.isInvalid())
        {
            launcherList.add(launcher);
        }
        else
        {
            unregisterLauncher(launcher);
        }
    }

    public static void unregisterLauncher(TileLauncherPrefab launcher)
    {
        launcherList.remove(launcher);
    }

    public static HashSet<TileLauncherPrefab> naFaSheQiInArea(Vector2 minVector, Vector2 maxVector)
    {
        HashSet<TileLauncherPrefab> returnArray = new HashSet<TileLauncherPrefab>();

        for (TileLauncherPrefab launcher : launcherList)
        {
            if (launcher.xCoord > minVector.x && launcher.xCoord < maxVector.x && launcher.zCoord > minVector.y && launcher.zCoord < maxVector.y)
            {
                returnArray.add(launcher);
            }
        }

        return returnArray;
    }

    public static HashSet<TileLauncherPrefab> getFaSheQi()
    {
        return launcherList;
    }
}
