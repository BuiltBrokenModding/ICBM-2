package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.ITurret;
import net.minecraft.entity.Entity;
import calclavia.api.icbm.IMissile;
import calclavia.api.icbm.ITarget;

/** Anti-Air target selection for the AA gun. Does some extended checking of flying targets to
 * prevent issues.
 * 
 * @author DarkGuardsman */
public class TurretAntiAirSelector extends TurretEntitySelector
{

    public TurretAntiAirSelector(ITurret turret)
    {
        super(turret);
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        if (entity instanceof ITarget)
        {
            return ((ITarget) entity).canBeTargeted(this.turretProvider.getTurret());
        }
        else if (entity instanceof IMissile)
        {
            return true;
        }
        return super.isEntityApplicable(entity);
    }

    @Override
    public boolean isValid(Entity entity)
    {
        if (entity instanceof ITarget)
        {
            return ((ITarget) entity).canBeTargeted(this.turretProvider.getTurret());
        }
        else if (entity instanceof IMissile)
        {
            //TODO: Check missile for impact with in area of protection up to + 200 of sentry max range.
            //If missile does not impact area then don't shoot at it, as well check if 
            //launcher is in area of protection to prevent friendly fire
            return ((IMissile) entity).getTicksInAir() > 5;
        }
        return super.isValid(entity);
    }

}
