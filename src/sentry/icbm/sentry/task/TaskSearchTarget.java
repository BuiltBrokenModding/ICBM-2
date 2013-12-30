package icbm.sentry.task;

import icbm.sentry.IAutoSentry;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.api.vector.Vector3;

public class TaskSearchTarget extends Task
{
    @SuppressWarnings("unchecked")
    @Override
    protected boolean onUpdateTask()
    {
        super.onUpdateTask();

        if (this.tileEntity instanceof IAutoSentry)
        {
            IAutoSentry sentry = this.tileEntity;

            if (sentry.getTarget() == null || !sentry.isValidTarget(sentry.getTarget()))
            {
                AxisAlignedBB bounds = sentry.getTargetingBox();

                List<Entity> entities = this.tileEntity.worldObj.getEntitiesWithinAABB(Entity.class, bounds);
                Entity currentTarget = null;

                /** Try to look for the owner within range and attack the entity attacking the owner
                 * if possible.
                 * 
                 * for (Entity entity : entities) { if (entity instanceof EntityPlayer) {
                 * EntityPlayer player = (EntityPlayer) entity; AccessLevel level =
                 * this.tileEntity.getPlatform().getUserAccess(player.username);
                 * 
                 * /** Checks to see if this player is friendly. If so, attempt to protect the
                 * player.
                 * 
                 * if (level.ordinal() >= AccessLevel.USER.ordinal()) { if (player.get != null) { if
                 * (sentry.isValidTarget(player.getLastAttackingEntity()) &&
                 * !player.getLastAttackingEntity().isDead) { currentTarget =
                 * player.getLastAttackingEntity(); break; } } } } } */

                if (currentTarget == null)
                {
                    double smallestDis = sentry.getDetectRange();

                    for (Entity entity : entities)
                    {
                        final double distance = this.tileEntity.getCenter().distanceTo(new Vector3(entity));

                        if (sentry.isValidTarget(entity) && distance <= smallestDis)
                        {
                            currentTarget = entity;
                            smallestDis = distance;
                        }
                    }
                }

                if (currentTarget != null)
                {
                    this.tileEntity.cancelRotation();
                    this.taskManager.addTask(new TaskKillTarget());
                    sentry.setTarget(currentTarget);
                    return false;
                }
                else if (this.tileEntity.lastRotateTick > this.world.rand.nextInt(30) + 10)
                {
                    this.tileEntity.rotateTo(this.world.rand.nextInt(360) - 180, this.world.rand.nextInt(30) - 10);
                }

                return true;
            }
            else
            {
                this.taskManager.addTask(new TaskKillTarget());
            }
        }

        return false;
    }
}
