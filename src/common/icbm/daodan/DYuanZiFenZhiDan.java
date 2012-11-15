package icbm.daodan;

import icbm.zhapin.ZhaPin;
import universalelectricity.core.vector.Vector3;

public class DYuanZiFenZhiDan extends DFenZhiDan
{
	public static final int MAX_CLUSTER = 4;

	protected DYuanZiFenZhiDan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void onTickFlight(EDaoDan missileObj)
	{
		if (missileObj.motionY < -0.5)
		{
			if (missileObj.missileCount < MAX_CLUSTER)
			{
				if (!missileObj.worldObj.isRemote)
				{
					Vector3 position = Vector3.get(missileObj);
					EDaoDan clusterMissile = new EDaoDan(missileObj.worldObj, position, Vector3.get(missileObj), ZhaPin.yuanZi.getID());
					missileObj.worldObj.spawnEntityInWorld(clusterMissile);
					clusterMissile.isCruise = true;
					clusterMissile.protectionTime = 20;
					clusterMissile.launchMissile(Vector3.add(missileObj.muBiao, new Vector3((missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30)));
				}

				missileObj.protectionTime = 20;
				missileObj.missileCount++;
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
		ZhaPin.createBaoZha(missileObj.worldObj, Vector3.get(missileObj), missileObj, ZhaPin.yuanZi.getID());
	}

	@Override
	public boolean isCruise()
	{
		return false;
	}
}
