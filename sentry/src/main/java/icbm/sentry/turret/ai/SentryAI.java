package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.ISentry;
import icbm.sentry.interfaces.ISentryContainer;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
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
            //For the moment work on AI code inside this class
            
            if (target == null)
            {
                target = findTarget(container.getSentry(), null, 100);
                return;
            }
            //TODO: if target &&  not facing, update rotation
            else
            {
                //TODO get position of sentry and of target. Then check if delta angle is +-2.3 degrees

                MovingObjectPosition endTarget = this.container.getSentry().getAimOffset().rayTraceEntities(this.container.world(), Vector3.fromCenter(this.target));
                if (endTarget != null)
                {
                    // Assuming the Vec3 Ray tracing includes the current pitch and angle given to the Sentry obj
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
        if (entity instanceof EntityPlayer)
        {
            if (((EntityPlayer) entity).capabilities.isCreativeMode)
            {
                return false;
            }
            //TODO using the access system check if the target is friendly
        }
        if (entity instanceof IAnimals)
        {
            return false;
        }
        if (entity instanceof INpc)
        {
            return false;
        }
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
            if(Math.abs(distanceA - distanceB) < 1.5)
            {
                float healthA = entityA.getHealth();
                float healthB = entityB.getHealth();
                return healthA < healthB ? -1 : (healthA > healthB ? 1 : 0);
            }
            return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
        }
    }
}
