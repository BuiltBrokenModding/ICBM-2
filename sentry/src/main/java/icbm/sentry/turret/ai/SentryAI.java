package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.ISentry;
import icbm.sentry.interfaces.ISentryContainer;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

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
            //later on we will migrate it to a program/AI system used by the robotic arm in resonant induction

            //TODO: if no target, find target
            if (target == null)
            {
                target = findTarget(container.getSentry(), null, 100);
            }
            //TODO: if target &&  not facing, update rotation
            //TODO: if target && aimed at target && can fire, fire weapon
        }
    }

    protected EntityLivingBase findTarget(ISentry sentry, IEntitySelector targetSelector, int range)
    {
        //TODO: get all targets within the range
        //TODO: filter targets threw the target selector
        //TODO: match target based on which can be seen
        //TODO: match targets which can be aimed at or are in potently line of sight
        //TODO: check targets for friend or foe
        //TODO: filter out creative players, mob bosses, and targets that can't be attacked
        //TODO: Finally select targets based on lowest heath, and closest to sentry

        List list = container.world().selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(container.x() + sentry.getCenterOffset().x, container.y() + sentry.getCenterOffset().y, container.z() + sentry.getCenterOffset().z, container.x() + sentry.getCenterOffset().x, container.y() + sentry.getCenterOffset().y, container.z() + sentry.getCenterOffset().z).expand(range, range, range), targetSelector);
        //Collections.sort(list, this.theNearestAttackableTargetSorter);
        return null;
    }
}
