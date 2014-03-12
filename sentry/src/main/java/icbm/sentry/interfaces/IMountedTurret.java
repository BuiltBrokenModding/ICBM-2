package icbm.sentry.interfaces;

import net.minecraft.entity.Entity;

/** @author DarkGuardsman */
public interface IMountedTurret
{
    /** Can the entity mount the sentry */
    public boolean canMount(Entity entity);
}
