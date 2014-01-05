package icbm.sentry.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

/** An interface applied to all automatic turrets.
 * 
 * @author Calclavia */
public interface IAutoSentry extends ISentry
{
    /** Get the current target of the Sentry */
    public Entity getTarget();

    /** Sets the target of the sentry if it doesn't have one or was overridden */
    public void setTarget(Entity target);
}
