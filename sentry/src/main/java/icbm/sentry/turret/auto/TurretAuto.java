package icbm.sentry.turret.auto;

import icbm.sentry.interfaces.IAutoTurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;

/**
 * A Class that functions as the AI of automatic turrets.
 */
public abstract class TurretAuto extends Turret implements IAutoTurret
{
	protected Entity target;

	public TurretAuto(ITurretProvider host)
	{
		super(host);
	}

	@Override
	public void update()
	{
		super.update();

		// Vector3 aim = new Vector3(this.getHost().x(), this.getHost().y(),
		// this.getHost().z()).add(getAimOffset());
		// getHost().world().spawnParticle("smoke", aim.x, aim.y, aim.z, 0, 0, 0);
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
