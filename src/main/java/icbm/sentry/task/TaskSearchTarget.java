package icbm.sentry.task;

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

        if (this.sentry().getTarget() == null || !this.sentry().isValidTarget(this.sentry().getTarget()))
        {
            AxisAlignedBB bounds = this.sentry().getTargetingBox();

            List<Entity> entities = this.world().getEntitiesWithinAABB(Entity.class, bounds);
            Entity currentTarget = null;

            if (currentTarget == null)
            {
                double smallestDis = this.sentry().maxTargetRange;
                final Vector3 center = this.sentry().pos();
                for (Entity entity : entities)
                {
                    final double distance = center.distance(entity);

                    if (this.sentry().isValidTarget(entity) && distance <= smallestDis)
                    {
                        currentTarget = entity;
                        smallestDis = distance;
                    }
                }
            }

            if (currentTarget != null)
            {
                this.sentry().cancelRotation();
                this.sentry().setTarget(currentTarget);
                return false;
            }
            else if (this.sentry().lastRotateTick > this.world().rand.nextInt(30) + 10)
            {
                this.sentry().rotateTo(this.world().rand.nextInt(360) - 180, this.world().rand.nextInt(30) - 10);
            }

            return true;
        }

        return false;
    }
}
