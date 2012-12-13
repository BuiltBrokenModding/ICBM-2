package icbm.common.daodan;

import icbm.common.daodan.EDaoDan.XingShi;
import universalelectricity.core.vector.Vector3;

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
		if (missileObj.motionY < -0.5)
		{
			if (missileObj.daoDanCount < MAX_CLUSTER)
			{
				if (!missileObj.worldObj.isRemote)
				{
					Vector3 position = new Vector3(missileObj);
					EDaoDan clusterMissile = new EDaoDan(missileObj.worldObj, position, new Vector3(missileObj), 0);
					clusterMissile.xingShi = XingShi.XIAO_DAN;
					clusterMissile.baoHuShiJian = 20;
					clusterMissile.faShe(Vector3.add(missileObj.muBiao, new Vector3((missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6)));
					missileObj.worldObj.spawnEntityInWorld(clusterMissile);
				}
				missileObj.baoHuShiJian = 20;
				missileObj.daoDanCount++;
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
	public boolean isCruise()
	{
		return false;
	}
}
