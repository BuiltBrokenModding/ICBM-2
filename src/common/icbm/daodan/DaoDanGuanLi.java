package icbm.daodan;


import java.util.ArrayList;
import java.util.List;

import universalelectricity.Vector2;

public class DaoDanGuanLi
{
	//Static methods
	private static List<EDaoDan> missiles = new ArrayList<EDaoDan>();
	
	public static void addMissile(EDaoDan missile)
	{
		cleanUpMissiles();
		
		if(!missiles.contains(missile))
		{
			missiles.add(missile);
		}
	}
	
	public static List<EDaoDan> getMissileInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpMissiles();
		List<EDaoDan> returnArray = new ArrayList<EDaoDan>();
		
		for(EDaoDan missile : missiles)
		{
	        if(missile.posX > minVector.x && missile.posX < maxVector.x && missile.posZ > minVector.y && missile.posZ < maxVector.y)
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
		for(int i = 0; i < missiles.size(); i++)
		{
			if(missiles.get(i) == null)
			{
				missiles.remove(i);
			}
			else if(missiles.get(i).isDead)
			{
				missiles.remove(i);
			}
		}
	}
}
