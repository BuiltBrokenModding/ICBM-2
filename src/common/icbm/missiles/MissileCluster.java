package icbm.missiles;

import icbm.EntityMissile;
import universalelectricity.Vector3;

public class MissileCluster extends Missile
{	
	public static final int MAX_CLUSTER = 12;
	
	protected MissileCluster(String name, int ID, int tier)
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
				EntityMissile clusterMissile = new EntityMissile(missileObj.worldObj, position, Vector3.get(missileObj), 0);
				clusterMissile.isCruise = true;
				clusterMissile.protectionTime = 20;
				clusterMissile.launchMissile(Vector3.add(missileObj.targetPosition, new Vector3((missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*6, (missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*6, (missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*6 )));
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
		missileObj.worldObj.createExplosion(missileObj, missileObj.posX, missileObj.posY, missileObj.posZ, 6F);
	}
	
	@Override
	public boolean isCruise() { return false; }
}
