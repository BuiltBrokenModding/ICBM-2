package icbm.explosion.zhapin.daodan;

import icbm.explosion.explosive.explosion.BzYuanZi;
import icbm.explosion.zhapin.Explosive;
import icbm.explosion.zhapin.daodan.EntityMissile.MissileType;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class DYuanZiFenZhiDan extends DFenZhiDan
{
	public DYuanZiFenZhiDan(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
	}

	public static final int MAX_CLUSTER = 4;

	@Override
	public void update(EntityMissile missileObj)
	{
		if (missileObj.motionY < -0.5)
		{
			if (missileObj.daoDanCount < MAX_CLUSTER)
			{
				if (!missileObj.worldObj.isRemote)
				{
					Vector3 position = new Vector3(missileObj);
					EntityMissile clusterMissile = new EntityMissile(missileObj.worldObj, position, new Vector3(missileObj), Explosive.yuanZi.getID());
					missileObj.worldObj.spawnEntityInWorld(clusterMissile);
					clusterMissile.missileType = MissileType.XIAO_DAN;
					clusterMissile.baoHuShiJian = 20;
					clusterMissile.launch(Vector3.add(missileObj.targetVector, new Vector3((missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 30)));
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
		new BzYuanZi(world, entity, x, y, z, 30, 50).setNuclear().explode();
	}

	@Override
	public boolean isCruise()
	{
		return false;
	}
}
