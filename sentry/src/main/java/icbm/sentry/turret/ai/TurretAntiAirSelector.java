package icbm.sentry.turret.ai;

import icbm.api.IMissile;
import icbm.sentry.interfaces.ITurret;
import net.minecraft.entity.Entity;

/** Anti-Air target selection for the AA gun. Does some extended checking of flying targets to
 * prevent issues.
 * 
 * @author DarkGuardsman */
public class TurretAntiAirSelector extends TurretEntitySelector
{

    public TurretAntiAirSelector(ITurret turret)
    {
        super(turret);
        this.target_mobs = false;
        this.target_players = false;
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        //TODO: Check missile for impact with in area of protection up to + 200 of sentry max range.
        //If missile does not impact area then don't shoot at it, as well check if 
        //launcher is in area of protection to preven friendly fire
        if (entity instanceof IMissile)
        {
            return true;
        }
        return super.isEntityApplicable(entity);
    }

    @Override
    public boolean isValid(Entity entity)
    {
        if (entity instanceof IMissile)
        {
            return true;
        }
        return super.isValid(entity);
    }

}
