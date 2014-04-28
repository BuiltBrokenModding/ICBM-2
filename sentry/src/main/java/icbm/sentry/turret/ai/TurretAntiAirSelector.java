package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.ITurret;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import calclavia.api.icbm.IMissile;
import calclavia.api.icbm.ITarget;

/** Anti-Air target selection for the AA gun. Does some extended checking of flying targets to
 * prevent issues.
 * 
 * @author DarkGuardsman */
public class TurretAntiAirSelector extends TurretEntitySelector
{
    protected boolean target_air = true;
    protected boolean target_missiles = true;

    public TurretAntiAirSelector(ITurret turret)
    {
        super(turret);
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        if (entity instanceof ITarget)
        {
            return target_air && ((ITarget) entity).canBeTargeted(this.turretProvider.getTurret());
        }
        else if (entity instanceof IMissile)
        {
            return target_missiles;
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

    @Override
    public void save(NBTTagCompound nbt)
    {
        nbt.setBoolean("vkill_missiles", target_missiles);
        nbt.setBoolean("vkill_air", target_air);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("vkill_missiles"))
            target_mobs = nbt.getBoolean("vkill_missiles");
        if (nbt.hasKey("vkill_air"))
            target_mobs = nbt.getBoolean("vkill_air");
    }

}
