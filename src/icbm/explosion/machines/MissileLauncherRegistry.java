package icbm.explosion.machines;

import java.util.HashSet;

import universalelectricity.core.vector.Vector2;

public class MissileLauncherRegistry
{
	private static HashSet<TileEntityLauncherPrefab> FA_SHE_QI = new HashSet<TileEntityLauncherPrefab>();

	public static void registerLauncher(TileEntityLauncherPrefab launcher)
	{
		if (!launcher.isInvalid())
		{
			FA_SHE_QI.add(launcher);
		}
		else
		{
			unregisterLauncher(launcher);
		}
	}

	public static void unregisterLauncher(TileEntityLauncherPrefab launcher)
	{
		FA_SHE_QI.remove(launcher);
	}

	public static HashSet<TileEntityLauncherPrefab> naFaSheQiInArea(Vector2 minVector, Vector2 maxVector)
	{
		HashSet<TileEntityLauncherPrefab> returnArray = new HashSet<TileEntityLauncherPrefab>();

		for (TileEntityLauncherPrefab launcher : FA_SHE_QI)
		{
			if (launcher.xCoord > minVector.x && launcher.xCoord < maxVector.x && launcher.zCoord > minVector.y && launcher.zCoord < maxVector.y)
			{
				returnArray.add(launcher);
			}
		}

		return returnArray;
	}

	public static HashSet<TileEntityLauncherPrefab> getFaSheQi()
	{
		return FA_SHE_QI;
	}
}
