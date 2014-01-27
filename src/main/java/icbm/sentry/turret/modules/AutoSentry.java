package icbm.sentry.turret.modules;

import icbm.sentry.turret.TileSentry;
import icbm.sentry.turret.sentryHandlers.Sentry;
import net.minecraft.entity.Entity;

public class AutoSentry extends Sentry
{
    protected Entity target;

    public AutoSentry(TileSentry host)
    {
        super(host);
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
