package icbm.sentry.interfaces;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

/** Interface for any sentry object that can be automated
 * 
 * @author DarkGuardsman */
public interface IAutoTurret extends ITurret
{
    /** Sets the target for the sentry */
    public void setTarget(Entity target);

    /** Gets the current target of the sentry */
    public Entity getTarget();

    /** Gets the filter for selecting new targets */
    public IEntitySelector getEntitySelector();
}
