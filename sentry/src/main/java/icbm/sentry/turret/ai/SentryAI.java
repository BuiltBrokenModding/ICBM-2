package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.ISentry;
import icbm.sentry.interfaces.ISentryContainer;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** AI for the sentry objects
 * 
 * @author DarkGuardsman */
public class SentryAI
{
    private ISentryContainer container;
    private EntityLivingBase target = null;

    public SentryAI(ISentryContainer container)
    {
        this.container = container;
    }

    public void update()
    {
        if (container != null && container.getSentry() != null && container.getSentry().automated())
        {
            //Find target if we have none
            if (target == null)
            {
                target = findTarget(container.getSentry(), new EntityCombatSelector(container), 100);
                return;
            }
            //if we have a target start doing rotation, and line of sight checks
            if (target != null)
            {
                Vector3 barrel = new Vector3();
                barrel.add(this.container.getSentry().getCenterOffset());
                barrel.add(this.container.getSentry().getAimOffset());
                barrel.rotate(this.container.yaw(), this.container.pitch());
                barrel.add(new Vector3(container.x(), container.y(), container.z()));

                MovingObjectPosition endTarget = barrel.rayTrace(this.container.world(), Vector3.fromCenter(this.target), true);
                //This ray trace is just for line of sight
                if (endTarget != null && endTarget.typeOfHit == EnumMovingObjectType.ENTITY)
                {
                    //TODO change getAngle out for the correct version
                    double deltaYaw = barrel.getAngle(new Vector3(target));
                    double deltaPitch = barrel.getAngle(new Vector3(target));
                    //TODO get position of sentry and of target. Then check if delta angle is +-2.3 degrees

                    this.container.getSentry().fire();
                }
                //TODO: if target && aimed at target && can fire, fire weapon
                // -- Might be able to split of Ability to fire to the Sentry obj itself? have the AI only fire commands.
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
            for (EntityLivingBase entity : list)
            {
                if (isValidTarget(entity))
                {
                    return entity;
                }
            }
        }
        return null;
    }

    /** Does some basic checks on the target to make sure it can be shot at without issues */
    protected boolean isValidTarget(EntityLivingBase entity)
    {
        //TODO apply ray trace to target
        //TODO filter out mob bosses to prevent cheating in boss fights
        return true;
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
