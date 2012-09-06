package icbm;

import icbm.machines.TileEntityRadarStation;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.Vector2;

public class RadarStationManager
{
	//Static methods
	private static List<TileEntityRadarStation> radarStations = new ArrayList<TileEntityRadarStation>();
	
	public static void addRadarStation(TileEntityRadarStation radarStation)
	{
		cleanUpRadarArray();
		
		if(!radarStations.contains(radarStation))
		{
			radarStations.add(radarStation);
		}
	}
	
	public static List<TileEntityRadarStation> getRadarStationsInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpRadarArray();
		List<TileEntityRadarStation> returnArray = new ArrayList<TileEntityRadarStation>();
		
		for(TileEntityRadarStation radarStation : radarStations)
		{
	        if(radarStation.xCoord > minVector.x && radarStation.xCoord < maxVector.x && radarStation.zCoord > minVector.y && radarStation.zCoord < maxVector.y)
	        {
	        	returnArray.add(radarStation);
	        }
		}
		
		return returnArray;
	}
	
	public static List<TileEntityRadarStation> getRadarStations()
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
