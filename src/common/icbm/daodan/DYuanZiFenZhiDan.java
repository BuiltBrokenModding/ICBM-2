package icbm.daodan;

import icbm.daodan.EDaoDan.XingShi;
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
			if (missileObj.daoDanCount < MAX_CLUSTER)
			{
				if (!missileObj.worldObj.isRemote)
				{
					Vector3 position = new Vector3(missileObj);
					EDaoDan clusterMissile = new EDaoDan(missileObj.worldObj, position, new Vector3(missileObj), ZhaPin.yuanZi.getID());
					missileObj.worldObj.spawnEntityInWorld(clusterMissile);
					clusterMissile.xingShi = XingShi.XIAO_DAN;
					clusterMissile.baoHuShiJian = 20;
					clusterMissile.faShe(Vector3.add(missileObj.muBiao, new Vector3((missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 30)));
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
		ZhaPin.createBaoZha(missileObj.worldObj, new Vector3(missileObj), missileObj, ZhaPin.yuanZi.getID());
	}

	@Override
	public boolean isCruise()
	{
		return false;
	}
}
