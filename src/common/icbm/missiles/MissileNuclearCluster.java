package icbm.missiles;

import icbm.EntityMissile;
import icbm.explosions.Explosive;
import universalelectricity.Vector3;

public class MissileNuclearCluster extends MissileCluster
{	
	public static final int MAX_CLUSTER = 4;
	
	protected MissileNuclearCluster(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void onTickFlight(EntityMissile missileObj)
	{
		if(missileObj.motionY < -0.5)
		{
			if(missileObj.missileCount < MAX_CLUSTER)
			{
				Vector3 position = Vector3.get(missileObj);
				EntityMissile clusterMissile = new EntityMissile(missileObj.worldObj, position, Vector3.get(missileObj), Explosive.Nuclear.getID());
				clusterMissile.isCruise = true;
				clusterMissile.protectionTime = 20;
				clusterMissile.launchMissile(Vector3.add(missileObj.targetPosition, new Vector3((missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*30, (missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*30, (missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*30)));
				missileObj.worldObj.spawnEntityInWorld(clusterMissile);
				missileObj.protectionTime = 20;
				missileObj.missileCount ++;
			}
			else
			{
				missileObj.setDead();
			}
		}
	}
	
	@Override
	public void onExplode(EntityMissile missileObj)
	{
		Explosive.createExplosion(missileObj.worldObj, Vector3.get(missileObj), missileObj, Explosive.Nuclear.getID());
	}
	
	@Override
	public boolean isCruise() { return false; }
}
