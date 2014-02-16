package icbm.sentry.turret.modules;

import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.entity.Entity;

public abstract class AutoSentry extends Sentry
{
    protected Entity target;

    public AutoSentry(TileTurret host)
    {
        super(host);
    }

    @Override
    public void updateLoop ()
    {

    }

    public boolean updateAI()
    {
        return true;
    }

    public void setTarget(Entity target)
    {
        this.target = target;
    }

    public Entity getTarget()
    {
        return this.target;
    }

}
