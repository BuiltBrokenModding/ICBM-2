package icbm.sentry.turret.modules;

import icbm.sentry.interfaces.IAutoSentry;
import icbm.sentry.interfaces.ISentryContainer;
import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponSystem;
import net.minecraft.entity.Entity;

/** A Class that functions as the AI of automatic sentries */
public abstract class AutoSentry extends Sentry implements IAutoSentry
{
    protected Entity target;

    public AutoSentry(ISentryContainer host)
    {
        super(host);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
    }

    @Override
    public void setTarget(Entity target)
    {
        this.target = target;
    }

    @Override
    public Entity getTarget()
    {
        return this.target;
    }

}
