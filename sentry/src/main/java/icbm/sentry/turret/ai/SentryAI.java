package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.IAutoSentry;
import icbm.sentry.interfaces.ISentry;
import icbm.sentry.interfaces.ISentryContainer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

/** AI for the sentry objects
 * 
 * @author DarkGuardsman */
public class SentryAI
{
    private ISentryContainer container;
    private EntitySelectorSentry entitySelector;
    private int rotationDelayTimer = 0;
    private int targetLostTimer = 0;

    public SentryAI(ISentryContainer container)
    {
        this.container = container;
        this.entitySelector = new EntitySelectorSentry(this.container);
    }

    public IAutoSentry sentry()
    {
        if (container != null && container.getSentry() != null && container.getSentry() instanceof IAutoSentry)
        {
            return (IAutoSentry) container.getSentry();
        }
        return null;
    }

    public void update(LookHelper lookHelper)
    {
        if (sentry() != null)
        {
            System.out.println("[SentryAI]Debug: Update tick \n");
            //Only get new target if the current is missing or it will switch targets each update
            if (sentry().getTarget() == null)
            {
                System.out.println("[SentryAI]Debug: Searching for target");
                sentry().setTarget(findTarget(container.getSentry(), this.entitySelector, this.container.getSentry().getRange()));
            }
            //If we have a target start aiming logic
            if (sentry().getTarget() != null)
            {
                System.out.println("[SentryAI]Debug: Targeting");
                Vector3 barrel = this.container.getSentry().getCenterOffset();
                barrel.add(this.container.getSentry().getAimOffset());
                barrel.rotate(this.container.yaw(), this.container.pitch());
                barrel.add(new Vector3(this.container.x(), this.container.y(), this.container.z()));

                if (lookHelper.canEntityBeSeen(sentry().getTarget()) && lookHelper.isTargetInBounds(sentry().getTarget()))
                {
                    System.out.println("[SentryAI]Debug: Target can be seen");
                    if (lookHelper.isLookingAt(sentry().getTarget(), 1.0F))
                    {
                        System.out.println("[SentryAI]Debug: Target locked and firing weapon");
                        this.container.getSentry().fire(sentry().getTarget());
                    }
                    else
                    {
                        System.out.println("[SentryAI]Debug: Powering servos to aim at target");
                        lookHelper.lookAtEntity(sentry().getTarget());
                    }
                    targetLostTimer = 0;
                }
                else
                {
                    System.out.println("[SentryAI]Debug: No Target");
                    //Drop the target after 2 seconds of no sight
                    if (targetLostTimer >= 40)
                    {
                        sentry().setTarget(null);
                    }
                    targetLostTimer++;
                }
            }
            else
            {
                //Only start random rotation after a second of no target
                if (targetLostTimer >= 20)
                {
                    if (this.rotationDelayTimer >= 10)
                    {
                        this.rotationDelayTimer = 0;
                        //lookHelper.lookAt(new Vector3(this.container.x(), this.container.y(), this.container.z()));
                        //TODO change the yaw rotation randomly
                        //Tip world().rand instead of making a new random object
                    }
                    this.rotationDelayTimer++;
                }
                targetLostTimer++;
            }
        }

    }

    @SuppressWarnings("unchecked")
    protected EntityLivingBase findTarget(ISentry sentry, IEntitySelector targetSelector, int range)
    {
        List<EntityLivingBase> list = container.world().selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(container.x() + sentry.getCenterOffset().x, container.y() + sentry.getCenterOffset().y, container.z() + sentry.getCenterOffset().z, container.x() + sentry.getCenterOffset().x, container.y() + sentry.getCenterOffset().y, container.z() + sentry.getCenterOffset().z).expand(range, range, range), targetSelector);
        Collections.sort(list, new ComparatorClosestEntity(new VectorWorld(container.world(), container.x() + sentry.getCenterOffset().x, container.y() + sentry.getCenterOffset().y, container.z() + sentry.getCenterOffset().z)));
        if (list != null && !list.isEmpty())
        {
            Vector3 centerPoint = new Vector3(sentry.getHost().x(), sentry.getHost().y(), sentry.getHost().z()).add(sentry.getCenterOffset());
            for (EntityLivingBase entity : list)
            {
                boolean flag_bounds = LookHelper.isTargetInBounds(centerPoint, Vector3.fromCenter(entity), sentry.getHost().getYawServo(), sentry.getHost().getPitchServo());
                boolean flag_sight = LookHelper.canEntityBeSeen(centerPoint, entity);
                if (flag_bounds && flag_sight)
                {
                    return entity;
                }
            }
        }
        return null;
    }

    //TODO: add options to this for reversing the targeting filter
    public static class ComparatorClosestEntity implements Comparator<EntityLivingBase>
    {
        private final VectorWorld location;

        public ComparatorClosestEntity(VectorWorld location)
        {
            this.location = location;
        }

        public int compare(EntityLivingBase entityA, EntityLivingBase entityB)
        {
            double distanceA = this.location.distance(entityA);
            double distanceB = this.location.distance(entityB);
            if (Math.abs(distanceA - distanceB) < 1.5)
            {
                float healthA = entityA.getHealth();
                float healthB = entityB.getHealth();
                return healthA < healthB ? -1 : (healthA > healthB ? 1 : 0);
            }
            return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
        }
    }
}
