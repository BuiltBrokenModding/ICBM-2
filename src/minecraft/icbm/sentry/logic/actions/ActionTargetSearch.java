package icbm.sentry.logic.actions;

import icbm.sentry.api.IAutoSentry;
import icbm.sentry.turret.TileEntityAutoTurret;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;

public class ActionTargetSearch extends Action
{
	@Override
	protected boolean onUpdateTask()
	{
		super.onUpdateTask();
		if (this.tileEntity instanceof IAutoSentry)
		{
			IAutoSentry sentry = (IAutoSentry) this.tileEntity;

			if (sentry.getTarget() == null || !sentry.isValidTarget(sentry.getTarget()))
			{
				AxisAlignedBB bounds = sentry.getTargetingBox();

				List<EntityLiving> ents = this.tileEntity.worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);
				double smallestDis = sentry.getDetectRange();

				EntityLiving currentTarget = null;
				for (EntityLiving entity : ents)
				{
					double dis = entity.getDistance(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
					if (sentry.isValidTarget((Entity) entity) && dis < smallestDis)
					{
						currentTarget = entity;
						smallestDis = dis;
					}
				}
				sentry.setTarget(currentTarget, true);
			}
		}
		return false;
	}

	/**
	 * does a ray trace to the Entity to see if the turret can see it
	 */
	public boolean canEntityBeSeen(Entity par1Entity, TileEntityAutoTurret turret)
	{
		return this.world.rayTraceBlocks(this.world.getWorldVec3Pool().getVecFromPool(turret.zCoord, turret.yCoord + (double) turret.getMuzzle().y, turret.zCoord), this.world.getWorldVec3Pool().getVecFromPool(par1Entity.posX, par1Entity.posY + (double) par1Entity.getEyeHeight(), par1Entity.posZ)) == null;
	}
}
