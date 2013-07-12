package icbm.zhapin.zhapin.daodan;

import icbm.api.IMissileLockable;
import icbm.core.ZhuYaoICBM;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzYaSuo;
import icbm.zhapin.muoxing.daodan.MMFanDan;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

/**
 * Antiballistic missile.
 * 
 * @author Calclavia
 * 
 */
public class DFanDan extends DaoDanTeBie
{
	public static final int ABMRange = 30;

	public DFanDan(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
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
				missileObj.worldObj.playSoundAtEntity(missileObj, ZhuYaoICBM.PREFIX + "targetlocked", 5F, 0.9F);
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
	public boolean isCruise()
	{
		return true;
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzYaSuo(world, entity, x, y, z, 6).setDestroyItems().explode();
	}

	@Override
	public MICBM getMuoXing()
	{
		return new MMFanDan();
	}
}
