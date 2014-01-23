package icbm.sentry.turret.sentries;

import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.TileSentry;
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
