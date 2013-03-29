package icbm.gangshao.actions;

import icbm.api.IAutoSentry;
import icbm.gangshao.terminal.AccessLevel;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class ActionSearchTarget extends Action
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

				List<EntityLiving> entities = this.tileEntity.worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);
				EntityLiving currentTarget = null;

				/**
				 * Try to look for the owner within range and attack the entity attacking the owner
				 * if possible.
				 */
				for (EntityLiving entity : entities)
				{
					if (entity instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) entity;
						AccessLevel level = this.tileEntity.getPlatform().getUserAccess(player.username);

						/**
						 * Checks to see if this player is friendly. If so, attempt to protect the
						 * player.
						 */
						if (level.ordinal() >= AccessLevel.USER.ordinal())
						{
							if (player.getLastAttackingEntity() != null)
							{
								if (sentry.isValidTarget(player.getLastAttackingEntity()) && !player.getLastAttackingEntity().isDead)
								{
									currentTarget = player.getLastAttackingEntity();
									break;
								}
							}
						}
					}
				}

				if (currentTarget == null)
				{
					double smallestDis = sentry.getDetectRange();

					for (EntityLiving entity : entities)
					{
						double distance = entity.getDistance(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);

						if (sentry.isValidTarget((Entity) entity) && distance < smallestDis)
						{
							currentTarget = entity;
							smallestDis = distance;
						}
					}
				}

				sentry.setTarget(currentTarget, true);
			}
		}

		return false;
	}
}
