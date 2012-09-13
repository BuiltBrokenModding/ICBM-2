package icbm.jiqi;


import java.util.ArrayList;
import java.util.List;

import universalelectricity.Vector2;

public class LeiDaGuanLi
{
	//Static methods
	private static List<TLeiDa> radarStations = new ArrayList<TLeiDa>();
	
	public static void addRadarStation(TLeiDa radarStation)
	{
		cleanUpRadarArray();
		
		if(!radarStations.contains(radarStation))
		{
			radarStations.add(radarStation);
		}
	}
	
	public static List<TLeiDa> getRadarStationsInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpRadarArray();
		List<TLeiDa> returnArray = new ArrayList<TLeiDa>();
		
		for(TLeiDa radarStation : radarStations)
		{
	        if(radarStation.xCoord > minVector.x && radarStation.xCoord < maxVector.x && radarStation.zCoord > minVector.y && radarStation.zCoord < maxVector.y)
	        {
	        	returnArray.add(radarStation);
	        }
		}
		
		return returnArray;
	}
	
	public static List<TLeiDa> getRadarStations()
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
