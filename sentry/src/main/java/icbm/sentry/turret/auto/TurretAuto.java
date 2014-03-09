package icbm.sentry.turret.auto;

import icbm.sentry.interfaces.IAutoTurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.ai.TurretEntitySelector;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

/** A Class that functions as the AI of automatic turrets. */
public abstract class TurretAuto extends Turret implements IAutoTurret
{
    protected Entity target;
    protected IEntitySelector selector;

    public TurretAuto(ITurretProvider host)
    {
        super(host);
        selector = new TurretEntitySelector(this);
    }

    @Override
    public void update()
    {
        super.update();

        if (!world().isRemote)
        {
            ai.update();
            getServo().update();
        }

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

    public IEntitySelector getEntitySelector()
    {
        return selector;
    }
}
