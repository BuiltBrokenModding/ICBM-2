package icbm.api.sentry;

import net.minecraft.entity.Entity;

/** An interface applied to all automated sentry guns
 * 
 * @author DarkGuardsman */
public interface IAutoSentry extends ISentryTile
{

    /** @param target */
    void setTarget(Entity target);

    /** @return */
    Entity getTarget();
}
