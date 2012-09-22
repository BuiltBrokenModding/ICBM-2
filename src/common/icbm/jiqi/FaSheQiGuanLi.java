package icbm.jiqi;

import icbm.extend.TLauncher;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.prefab.Vector2;

public class FaSheQiGuanLi
{
	//Static methods
	private static List<TLauncher> launcherScreens = new ArrayList<TLauncher>();
	
	public static void addLauncher(TLauncher launcher)
	{
		cleanUpMissiles();
		
		if(!launcherScreens.contains(launcher))
		{
			launcherScreens.add(launcher);
		}
	}
	
	public static List<TLauncher> getLaunchersInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpMissiles();
		List<TLauncher> returnArray = new ArrayList<TLauncher>();
		
		for(TLauncher launcher : launcherScreens)
		{
	        if(launcher.xCoord > minVector.x && launcher.xCoord < maxVector.x && launcher.zCoord > minVector.y && launcher.zCoord < maxVector.y)
	        {
	        	returnArray.add(launcher);
	        }
		}
		
		return returnArray;
	}
	
	public static List<TLauncher> getLaunchers()
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
