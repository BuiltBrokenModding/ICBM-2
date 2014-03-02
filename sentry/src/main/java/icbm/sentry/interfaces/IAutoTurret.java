package icbm.sentry.interfaces;

import net.minecraft.entity.Entity;

public interface IAutoTurret extends ITurret
{
    public void setTarget(Entity target);

    public Entity getTarget();
}
