package icbm.missiles;

import icbm.EntityMissile;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.Vector2;

public class MissileManager
{
	//Static methods
	private static List<EntityMissile> missiles = new ArrayList<EntityMissile>();
	
	public static void addMissile(EntityMissile missile)
	{
		cleanUpMissiles();
		
		if(!missiles.contains(missile))
		{
			missiles.add(missile);
		}
	}
	
	public static List<EntityMissile> getMissileInArea(Vector2 minVector, Vector2 maxVector)
	{
		cleanUpMissiles();
		List<EntityMissile> returnArray = new ArrayList<EntityMissile>();
		
		for(EntityMissile missile : missiles)
		{
	        if(missile.posX > minVector.x && missile.posX < maxVector.x && missile.posZ > minVector.y && missile.posZ < maxVector.y)
	        {
	        	returnArray.add(missile);
	        }
		}
		
		return returnArray;
	}
	
	public static List<EntityMissile> getMissiles()
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
