package icbm.jiqi;

import icbm.extend.TFaSheQi;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.prefab.Vector2;

public class FaSheQiGuanLi
{
	//Static methods
	private static List<TFaSheQi> launcherScreens = new ArrayList<TFaSheQi>();
	
	public static void addLauncher(TFaSheQi launcher)
	{
		cleanUpMissiles();
		
		if(!launcherScreens.contains(launcher))
		{
			launcherScreens.add(launcher);
		}
	}
	
	public static List<TFaSheQi> getLaunchersInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpMissiles();
		List<TFaSheQi> returnArray = new ArrayList<TFaSheQi>();
		
		for(TFaSheQi launcher : launcherScreens)
		{
	        if(launcher.xCoord > minVector.x && launcher.xCoord < maxVector.x && launcher.zCoord > minVector.y && launcher.zCoord < maxVector.y)
	        {
	        	returnArray.add(launcher);
	        }
		}
		
		return returnArray;
	}
	
	public static List<TFaSheQi> getLaunchers()
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
