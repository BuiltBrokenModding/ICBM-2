package icbm.gangshao.task;

import icbm.api.sentry.IAutoSentry;
import universalelectricity.core.vector.Vector3;

public class TaskKillTarget extends TaskSearchTarget
{
	int ticksTarget = 0;/* ticks since target has been seen */

	@Override
	protected boolean onUpdateTask()
	{
		super.onUpdateTask();

		if (this.tileEntity instanceof IAutoSentry)
		{
			IAutoSentry turret = (IAutoSentry) this.tileEntity;
			System.out.println("KILLING");

			if (!turret.isValidTarget(turret.getTarget()))
			{
				turret.setTarget(null, true);
				return false;
			}
			else if (turret.canActivateWeapon())
			{
				turret.onWeaponActivated();
			}
			else
			{
				this.tileEntity.lookHelper.lookAt(new Vector3(((IAutoSentry) this.tileEntity).getTarget()));
			}
		}

		return true;
	}
}
