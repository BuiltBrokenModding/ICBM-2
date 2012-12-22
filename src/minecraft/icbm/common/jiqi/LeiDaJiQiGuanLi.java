package icbm.common.jiqi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;

public class LeiDaJiQiGuanLi
{
	// Static methods
	private static List<TileEntityElectricityReceiver> jiQi = new ArrayList<TileEntityElectricityReceiver>();

	public static void register(TileEntityElectricityReceiver radarStation)
	{
		if (!jiQi.contains(radarStation))
		{
			jiQi.add(radarStation);
		}
	}

	public static void unregister(TileEntityElectricityReceiver radarStation)
	{
		if (jiQi.contains(radarStation))
		{
			jiQi.remove(radarStation);
		}
	}

	public static List<TileEntityElectricityReceiver> getJiQiInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpArray();
		List<TileEntityElectricityReceiver> returnArray = new ArrayList<TileEntityElectricityReceiver>();

		for (TileEntityElectricityReceiver radarStation : jiQi)
		{
			if (radarStation.xCoord > minVector.x && radarStation.xCoord < maxVector.x && radarStation.zCoord > minVector.y && radarStation.zCoord < maxVector.y)
			{
				returnArray.add(radarStation);
			}
		}

		return returnArray;
	}

	public static List<TileEntityElectricityReceiver> getAllJiQi()
	{
		cleanUpArray();
		return jiQi;
	}

	public static void cleanUpArray()
	{
		try
		{
			Iterator it = jiQi.iterator();

			while (it.hasNext())
			{
				TileEntityElectricityReceiver tileEntity = (TileEntityElectricityReceiver) it.next();

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
			System.out.println("Failed to clean up radar list properly.");
			e.printStackTrace();
		}
	}
}
