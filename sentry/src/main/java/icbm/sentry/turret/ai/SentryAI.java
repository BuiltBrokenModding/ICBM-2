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
    private EntityCombatSelector selector;
    private int rotationTimer;

    public SentryAI (ISentryContainer container)
    {
        this.container = container;
        this.selector = new EntityCombatSelector(this.container);
        this.rotationTimer = rnd.nextInt(60);
    }

    public void update (LookHelper lookHelper)
    {
        if (container != null && container.getSentry() != null && container.getSentry().automated())
        {
            // Need to scan for target as Caching them causes new ones to be ignored
            this.target = findTarget(container.getSentry(), this.selector, this.container.getSentry().getRange());
            this.rotationTimer--;

            if (target != null)
            {
                //if we have a target start doing rotation, and line of sight checks

                Vector3 barrel = new Vector3();
                barrel.add(this.container.getSentry().getCenterOffset());
                barrel.add(this.container.getSentry().getAimOffset());
                barrel.rotate(this.container.yaw(), this.container.pitch());
                barrel.add(new Vector3(container.x(), container.y(), container.z()));

                if (lookHelper.isLookingAt(target, 1.0F))
                {
                    //TODO change getAngle out for the correct version
                    double deltaYaw = barrel.getAngle(new Vector3(target));
                    double deltaPitch = barrel.getAngle(new Vector3(target));
                    this.container.getSentry().fire(new Vector3(target));
                    this.target.attackEntityFrom(TurretDamageSource.TurretProjectile, 1.0F);
                }

                if (this.rotationTimer <= 0)
                {
                    System.out.println("rotation: " + this.rotationTimer);
                    System.out.println("Yaw: " + this.container.yaw() + " Pitch: " + this.container.pitch());
                    this.rotationTimer = 60;
                    aimToTarget(lookHelper);
                }
            }
        }

    }

    public void aimToTarget (LookHelper lookHelper)
    {
        if (this.target != null)
        {
            lookHelper.lookAtEntity(this.target);
            this.rotationTimer = 10;
            System.out.println(this.target);
        }
        else
            lookHelper.lookAt(new Vector3(this.container.x(), this.container.y() + 50, this.container.z()));
    }

    @SuppressWarnings("unchecked")
    protected EntityLivingBase findTarget (ISentry sentry, IEntitySelector targetSelector, int range)
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
    protected boolean isValidTarget (EntityLivingBase entity)
    {
        //TODO filter out mob bosses to prevent cheating in boss fights
        return true;
    }

    //TODO: add options to this for reversing the targeting filter
    public static class ComparatorClosestEntity implements Comparator<EntityLivingBase>
    {
        private final VectorWorld location;

        public ComparatorClosestEntity (VectorWorld location)
        {
            this.location = location;
        }

        public int compare (EntityLivingBase entityA, EntityLivingBase entityB)
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
