package icbm.sentry.interfaces;

import net.minecraft.entity.Entity;

public interface IAutoSentry extends ISentry
{
    public void setTarget(Entity target);

    public Entity getTarget();
}
