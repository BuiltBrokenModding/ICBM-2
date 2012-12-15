package icbm.common.jiqi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import universalelectricity.core.vector.Vector2;

public class LeiDaGuanLi
{
	// Static methods
	private static List<TLeiDaTai> radarStations = new ArrayList<TLeiDaTai>();

	public static void addRadarStation(TLeiDaTai radarStation)
	{
		if (!radarStations.contains(radarStation))
		{
			radarStations.add(radarStation);
		}
	}

	public static List<TLeiDaTai> getRadarStationsInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpRadarArray();
		List<TLeiDaTai> returnArray = new ArrayList<TLeiDaTai>();

		for (TLeiDaTai radarStation : radarStations)
		{
			if (radarStation.xCoord > minVector.x && radarStation.xCoord < maxVector.x && radarStation.zCoord > minVector.y && radarStation.zCoord < maxVector.y)
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
		try
		{
			Iterator it = radarStations.iterator();

			while (it.hasNext())
			{
				TLeiDaTai tileEntity = (TLeiDaTai) it.next();

				if (tileEntity == null)
				{
					it.remove();
				}
				else if (tileEntity.isInvalid())
				{
					it.remove();
				}
				else if (tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity)
				{
					it.remove();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to clean up Radar properly.");
			e.printStackTrace();
		}
	}
}
