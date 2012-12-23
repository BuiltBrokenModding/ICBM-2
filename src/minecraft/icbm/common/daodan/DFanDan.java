package icbm.common.daodan;

import icbm.api.IMissileLockable;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
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
			Vector3 guJiDiDian = new Vector3(missileObj);

			if (missileObj instanceof EDaoDan)
			{
				if (((EDaoDan) missileObj.lockedTarget).zhengZaiBaoZha)
				{
					missileObj.explode();
					return;
				}

				guJiDiDian = ((EDaoDan) missileObj.lockedTarget).guJi(4);
			}

			missileObj.motionX = (guJiDiDian.x - missileObj.posX) * (0.3F);
			missileObj.motionY = (guJiDiDian.y - missileObj.posY) * (0.3F);
			missileObj.motionZ = (guJiDiDian.z - missileObj.posZ) * (0.3F);

			return;
		}

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(missileObj.posX - ABMRange, missileObj.posY - ABMRange, missileObj.posZ - ABMRange, missileObj.posX + ABMRange, missileObj.posY + ABMRange, missileObj.posZ + ABMRange);
		Entity nearestEntity = missileObj.worldObj.findNearestEntityWithinAABB(EDaoDan.class, bounds, missileObj);

		if (nearestEntity instanceof IMissileLockable)
		{
			if (((IMissileLockable) nearestEntity).canLock())
			{
				// Lock target onto missileObj missile
				missileObj.lockedTarget = nearestEntity;
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
