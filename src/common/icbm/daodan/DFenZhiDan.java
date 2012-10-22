package icbm.daodan;

import universalelectricity.prefab.Vector3;

public class DFenZhiDan extends DaoDan
{	
	public static final int MAX_CLUSTER = 12;
	
	protected DFenZhiDan(String name, int ID, int tier)
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
				if(!missileObj.worldObj.isRemote)
				{
					Vector3 position = Vector3.get(missileObj);
					EDaoDan clusterMissile = new EDaoDan(missileObj.worldObj, position, Vector3.get(missileObj), 0);
					clusterMissile.isCruise = true;
					clusterMissile.protectionTime = 20;
					clusterMissile.launchMissile(Vector3.add(missileObj.muBiao, new Vector3((missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*6, (missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*6, (missileObj.missileCount-MAX_CLUSTER/2)*Math.random()*6 )));
					missileObj.worldObj.spawnEntityInWorld(clusterMissile);
				}
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
		missileObj.worldObj.createExplosion(missileObj, missileObj.posX, missileObj.posY, missileObj.posZ, 6F, true);
	}
	
	@Override
	public boolean isCruise() { return false; }
}
