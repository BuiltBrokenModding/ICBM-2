package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.ISentry;
import icbm.sentry.interfaces.ISentryContainer;
import icbm.sentry.turret.weapon.TurretDamageSource;
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
import java.util.Random;

/** AI for the sentry objects
 * 
 * @author DarkGuardsman */
public class SentryAI
{
    Random rnd = new Random();
    private ISentryContainer container;
    private EntityLivingBase target = null;
    private EntitySelectorSentry selector;
    private int rotationTimer = 0;
    private int targetCoolDown = 0;

    public SentryAI(ISentryContainer container)
    {
        this.container = container;
        this.selector = new EntitySelectorSentry(this.container);
    }

    public void update(LookHelper lookHelper)
    {
        if (container != null && container.getSentry() != null && container.getSentry().automated())
        {
            //Only get new target if the current is missing or it will switch targets each update
            if (target == null)
            {
                this.target = findTarget(container.getSentry(), this.selector, this.container.getSentry().getRange());
            }
            //If we have a target start aiming logic
            if (target != null)
            {
                Vector3 barrel = this.container.getSentry().getCenterOffset();
                barrel.add(this.container.getSentry().getAimOffset());
                barrel.rotate(this.container.yaw(), this.container.pitch());
                barrel.add(new Vector3(this.container.x(), this.container.y(), this.container.z()));

                if (lookHelper.canEntityBeSeen(this.target))
                {
                    targetCoolDown = 0;
                    if (lookHelper.isLookingAt(this.target, 1.0F))
                    {
                        this.container.getSentry().fire(this.target);
                    }
                    else
                    {
                        lookHelper.lookAtEntity(this.target);
                    }
                }
                else
                {
                    //Drop the target after 2 seconds of no sight
                    targetCoolDown++;
                    if (targetCoolDown >= 40)
                    {
                        target = null;
                    }
                }
            }
            else
            {
                //Only start random rotation after a second of no target
                if (targetCoolDown >= 20)
                {
                    if (this.rotationTimer >= 10)
                    {
                        this.rotationTimer = 0;
                        //lookHelper.lookAt(new Vector3(this.container.x(), this.container.y(), this.container.z()));
                        //TODO change the yaw rotation randomly
                    }
                    else
                    {
                        this.rotationTimer++;
                    }
                }
                else
                {
                    targetCoolDown++;
                }
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
