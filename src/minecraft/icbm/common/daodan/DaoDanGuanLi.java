package icbm.common.daodan;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.core.vector.Vector2;
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

	public static List<EDaoDan> getMissileInArea(Vector2 vector, int radius)
	{
		cleanUpMissiles();
		List<EDaoDan> returnArray = new ArrayList<EDaoDan>();

		for (EDaoDan missile : missiles)
		{
			if (Vector2.distance(vector, new Vector3(missile).toVector2()) <= radius)
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
