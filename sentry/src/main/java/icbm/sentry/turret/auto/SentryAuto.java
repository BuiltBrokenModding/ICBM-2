package icbm.sentry.turret.auto;

import icbm.sentry.interfaces.IAutoSentry;
import icbm.sentry.interfaces.ISentryContainer;
import icbm.sentry.turret.Sentry;
import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;

/** A Class that functions as the AI of automatic sentries */
public abstract class SentryAuto extends Sentry implements IAutoSentry
{
	protected Entity target;

	public SentryAuto(ISentryContainer host)
	{
		super(host);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

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
