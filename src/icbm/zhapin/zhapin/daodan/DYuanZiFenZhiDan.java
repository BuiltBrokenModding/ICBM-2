package icbm.zhapin.zhapin.daodan;

import icbm.zhapin.baozha.bz.BzYuanZi;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.EDaoDan.XingShi;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class DYuanZiFenZhiDan extends DFenZhiDan {
	public DYuanZiFenZhiDan(String mingZi, int tier) {
		super(mingZi, tier);
		this.hasBlock = false;
	}

	public static final int MAX_CLUSTER = 4;

	@Override
	public void update(EDaoDan missileObj) {
		if (missileObj.motionY < -0.5) {
			if (missileObj.daoDanCount < MAX_CLUSTER) {
				if (!missileObj.worldObj.isRemote) {
					Vector3 position = new Vector3(missileObj);
					EDaoDan clusterMissile = new EDaoDan(missileObj.worldObj,
							position, new Vector3(missileObj),
							ZhaPin.yuanZi.getID());
					missileObj.worldObj.spawnEntityInWorld(clusterMissile);
					clusterMissile.xingShi = XingShi.XIAO_DAN;
					clusterMissile.baoHuShiJian = 20;
					clusterMissile.launch(Vector3.add(missileObj.muBiao,
							new Vector3(
									(missileObj.daoDanCount - MAX_CLUSTER / 2)
											* Math.random() * 30,
									(missileObj.daoDanCount - MAX_CLUSTER / 2)
											* Math.random() * 30,
									(missileObj.daoDanCount - MAX_CLUSTER / 2)
											* Math.random() * 30)));
				}

				missileObj.baoHuShiJian = 20;
				missileObj.daoDanCount++;
			} else {
				missileObj.setDead();
			}
		}
	}

	@Override
	public void createExplosion(World world, double x, double y, double z,
			Entity entity) {
		new BzYuanZi(world, entity, x, y, z, 30, 50).setNuclear().explode();
	}

	@Override
	public boolean isCruise() {
		return false;
	}
}
