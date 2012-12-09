package icbm.daodan;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import universalelectricity.core.vector.Vector3;

public class DFanDan extends DaoDan
{
	public static final int ABMRange = 30;

	protected DFanDan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void onTickFlight(EDaoDan missileObj)
	{
		if (missileObj.lockedTarget != null)
		{
			if (!missileObj.lockedTarget.zhengZaiBaoZha)
			{
				Vector3 guJiDiDian = missileObj.lockedTarget.guJi(4);

				System.out.println(new Vector3(missileObj) + " vs " + guJiDiDian);

				missileObj.motionX = (guJiDiDian.x - missileObj.posX) * (0.3F);
				missileObj.motionY = (guJiDiDian.y - missileObj.posY) * (0.3F);
				missileObj.motionZ = (guJiDiDian.z - missileObj.posZ) * (0.3F);
			}
			else
			{
				missileObj.explode();
			}

			return;
		}

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(missileObj.posX - ABMRange, missileObj.posY - ABMRange, missileObj.posZ - ABMRange, missileObj.posX + ABMRange, missileObj.posY + ABMRange, missileObj.posZ + ABMRange);
		Entity nearestEntity = missileObj.worldObj.findNearestEntityWithinAABB(EDaoDan.class, bounds, missileObj);

		if (nearestEntity instanceof EDaoDan)
		{
			if (((EDaoDan) nearestEntity).feiXingTick >= 0)
			{
				// Lock target onto missileObj
				// missile
				missileObj.lockedTarget = (EDaoDan) nearestEntity;
				missileObj.didTargetLockBefore = true;
				missileObj.worldObj.playSoundAtEntity(missileObj, "icbm.targetlocked", 5F, 0.9F);
			}
		}
		else
		{
			missileObj.motionX = missileObj.xXiangCha / missileObj.feiXingShiJian;
			missileObj.motionZ = missileObj.zXiangCha / missileObj.feiXingShiJian;

			if (missileObj.didTargetLockBefore == true)
			{
				missileObj.explode();
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
