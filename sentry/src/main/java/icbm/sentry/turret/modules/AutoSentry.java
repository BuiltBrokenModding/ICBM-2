package icbm.sentry.turret.modules;

import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponSystem;
import net.minecraft.entity.Entity;

/** A Class that functions as the AI of automatic sentries */
public abstract class AutoSentry extends Sentry
{
    protected Entity target;
    protected WeaponSystem weaponSystem;

    public AutoSentry(TileTurret host)
    {
        super(host);

    }

    @Override
    public void updateEntity()
    {

    }

    public void setTarget(Entity target)
    {
        this.target = target;
    }

    public Entity getTarget()
    {
        return this.target;
    }

    @Override
    public boolean automated()
    {
        return true;
    }

}
