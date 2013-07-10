package icbm.zhapin.zhapin.daodan;

import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzYaSuo;
import icbm.zhapin.muoxing.daodan.MMFenZiDan;
import icbm.zhapin.zhapin.daodan.EDaoDan.XingShi;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class DFenZhiDan extends DaoDan
{
	public DFenZhiDan(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
	}

	public static final int MAX_CLUSTER = 12;

	@Override
	public void update(EDaoDan missileObj)
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
					clusterMissile.launch(Vector3.add(missileObj.muBiao, new Vector3((missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6)));
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
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzYaSuo(world, entity, x, y, z, 6).setDestroyItems().explode();
	}

	@Override
	public boolean isCruise()
	{
		return false;
	}

	@Override
	public MICBM getMuoXing()
	{
		return new MMFenZiDan();
	}
}
