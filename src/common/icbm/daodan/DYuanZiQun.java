package icbm.daodan;

import icbm.zhapin.ZhaPin;
import universalelectricity.prefab.Vector3;

public class DYuanZiQun extends DQun
{	
	public static final int MAX_CLUSTER = 4;
	
	protected DYuanZiQun(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void onTickFlight(EDaoDan missileObj)
	{
		if(missileObj.motionY < -0.5)
		{
			if(missileObj.missileCount < MAX_CLUSTER)
			{
				Vector3 position = Vector3.get(missileObj);
				EDaoDan clusterMissile = new EDaoDan(missileObj.worldObj, position, Vector3.get(missileObj), ZhaPin.Nuclear.getID());
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
	public void onExplode(EDaoDan missileObj)
	{
		ZhaPin.createBaoZha(missileObj.worldObj, Vector3.get(missileObj), missileObj, ZhaPin.Nuclear.getID());
	}
	
	@Override
	public boolean isCruise() { return false; }
}
