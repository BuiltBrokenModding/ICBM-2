package icbm.zhapin.daodan;

import icbm.api.IMissileLockable;
import icbm.api.explosion.ExplosionEvent.PostExplosionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.core.vector.Vector3;

/**
 * Antiballistic missile.
 * 
 * @author Calclavia
 * 
 */
public class DFanDan extends DaoDan
{
	public static final int ABMRange = 30;

	protected DFanDan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void update(EDaoDan missileObj)
	{
		if (missileObj.lockedTarget != null)
		{
			Vector3 guJiDiDian = new Vector3(missileObj.lockedTarget);

			if (missileObj.lockedTarget.isDead)
			{
				missileObj.explode();
				return;
			}

			if (missileObj.lockedTarget instanceof IMissileLockable)
			{
				guJiDiDian = ((IMissileLockable) missileObj.lockedTarget).getPredictedPosition(4);
			}

			missileObj.motionX = (guJiDiDian.x - missileObj.posX) * (0.3F);
			missileObj.motionY = (guJiDiDian.y - missileObj.posY) * (0.3F);
			missileObj.motionZ = (guJiDiDian.z - missileObj.posZ) * (0.3F);

			return;
		}

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(missileObj.posX - ABMRange, missileObj.posY - ABMRange, missileObj.posZ - ABMRange, missileObj.posX + ABMRange, missileObj.posY + ABMRange, missileObj.posZ + ABMRange);
		// TODO: Check if this works.
		Entity nearestEntity = missileObj.worldObj.findNearestEntityWithinAABB(IMissileLockable.class, bounds, missileObj);

		if (nearestEntity instanceof IMissileLockable)
		{
			if (((IMissileLockable) nearestEntity).canLock(missileObj))
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
		MinecraftForge.EVENT_BUS.post(new PostExplosionEvent(missileObj.worldObj, missileObj.posX, missileObj.posY, missileObj.posZ, this));
	}

	@Override
	public float getRadius()
	{
		return 6;
	}

	@Override
	public double getEnergy()
	{
		return 100;
	}

	@Override
	public boolean isCruise()
	{
		return true;
	}
}
