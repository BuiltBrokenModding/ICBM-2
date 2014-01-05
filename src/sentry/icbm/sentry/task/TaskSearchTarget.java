package icbm.sentry.task;

import icbm.sentry.interfaces.IAutoSentry;

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
      

            if (this.tileEntity.getTarget() == null || !this.tileEntity.isValidTarget(this.tileEntity.getTarget()))
            {
                AxisAlignedBB bounds = this.tileEntity.getTargetingBox();

                List<Entity> entities = this.tileEntity.worldObj.getEntitiesWithinAABB(Entity.class, bounds);
                Entity currentTarget = null;

                if (currentTarget == null)
                {
                    double smallestDis = this.tileEntity.maxTargetRange;

                    for (Entity entity : entities)
                    {
                        final double distance = this.tileEntity.getCenter().distance(new Vector3(entity));

                        if (this.tileEntity.isValidTarget(entity) && distance <= smallestDis)
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
                    this.tileEntity.setTarget(currentTarget);
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

        return false;
    }
}
