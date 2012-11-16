package icbm.daodan;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.core.vector.Region2;
import universalelectricity.core.vector.Vector3;

public class DaoDanGuanLi
{
	// Static methods
	private static List<EDaoDan> missiles = new ArrayList<EDaoDan>();

	public static void addMissile(EDaoDan missile)
	{
		cleanUpMissiles();

		if (!missiles.contains(missile))
		{
			missiles.add(missile);
		}
	}

	public static List<EDaoDan> getMissileInArea(Region2 region)
	{
		cleanUpMissiles();
		List<EDaoDan> returnArray = new ArrayList<EDaoDan>();

		for (EDaoDan missile : missiles)
		{
			if (region.isIn(Vector3.get(missile).toVector2()))
			{
				returnArray.add(missile);
			}
		}

		return returnArray;
	}

	public static List<EDaoDan> getMissiles()
	{
		cleanUpMissiles();
		return missiles;
	}

	public static void cleanUpMissiles()
	{
		for (int i = 0; i < missiles.size(); i++)
		{
			if (missiles.get(i) == null)
			{
				missiles.remove(i);
			}
			else if (missiles.get(i).isDead)
			{
				missiles.remove(i);
			}
		}
	}
}
