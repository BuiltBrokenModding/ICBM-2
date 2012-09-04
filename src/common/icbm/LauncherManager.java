package icbm;

import icbm.extend.TileEntityLauncher;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.Vector2;

public class LauncherManager
{
	//Static methods
	private static List<TileEntityLauncher> launcherScreens = new ArrayList<TileEntityLauncher>();
	
	public static void addLauncher(TileEntityLauncher launcher)
	{
		cleanUpMissiles();
		
		if(!launcherScreens.contains(launcher))
		{
			launcherScreens.add(launcher);
		}
	}
	
	public static List<TileEntityLauncher> getLaunchersInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpMissiles();
		List<TileEntityLauncher> returnArray = new ArrayList<TileEntityLauncher>();
		
		for(TileEntityLauncher launcher : launcherScreens)
		{
	        if(launcher.xCoord > minVector.x && launcher.xCoord < maxVector.x && launcher.zCoord > minVector.y && launcher.zCoord < maxVector.y)
	        {
	        	returnArray.add(launcher);
	        }
		}
		
		return returnArray;
	}
	
	public static List<TileEntityLauncher> getLaunchers()
	{
		cleanUpMissiles();
		return launcherScreens;
	}
	
	public static void cleanUpMissiles()
	{
		for(int i = 0; i < launcherScreens.size(); i++)
		{
			if(launcherScreens.get(i) == null)
			{
				launcherScreens.remove(i);
			}
			else if(launcherScreens.get(i).isInvalid())
			{
				launcherScreens.remove(i);
			}
		}
	}
}
