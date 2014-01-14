package icbm.sentry.interfaces;

import net.minecraft.entity.Entity;

/** An interface applied to all automatic turrets.
 * 
 * @author DarkGuardsman */
public interface IAutoSentry extends ISentry
{

    /** @param target */
    void setTarget(Entity target);

    /** @return */
    Entity getTarget();
}
