package icbm.jiqi;


import java.util.ArrayList;
import java.util.List;

import universalelectricity.prefab.Vector2;

public class LeiDaGuanLi
{
	//Static methods
	private static List<TLeiDaTai> radarStations = new ArrayList<TLeiDaTai>();
	
	public static void addRadarStation(TLeiDaTai radarStation)
	{
		cleanUpRadarArray();
		
		if(!radarStations.contains(radarStation))
		{
			radarStations.add(radarStation);
		}
	}
	
	public static List<TLeiDaTai> getRadarStationsInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpRadarArray();
		List<TLeiDaTai> returnArray = new ArrayList<TLeiDaTai>();
		
		for(TLeiDaTai radarStation : radarStations)
		{
	        if(radarStation.xCoord > minVector.x && radarStation.xCoord < maxVector.x && radarStation.zCoord > minVector.y && radarStation.zCoord < maxVector.y)
	        {
	        	returnArray.add(radarStation);
	        }
		}
		
		return returnArray;
	}
	
	public static List<TLeiDaTai> getRadarStations()
	{
		cleanUpRadarArray();
		return radarStations;
	}
	
	public static void cleanUpRadarArray()
	{
		for(int i = 0; i < radarStations.size(); i++)
		{
			if(radarStations.get(i) == null)
			{
				radarStations.remove(i);
			}
			else if(radarStations.get(i).isInvalid())
			{
				radarStations.remove(i);
			}
		}
	}
}
