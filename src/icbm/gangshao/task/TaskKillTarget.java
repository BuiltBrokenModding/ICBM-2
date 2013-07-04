package icbm.gangshao.task;

import icbm.gangshao.IAutoSentry;

public class TaskKillTarget extends TaskSearchTarget
{
	int ticksTarget = 0;/* ticks since target has been seen */

	@Override
	protected boolean onUpdateTask()
	{
		super.onUpdateTask();

		if (this.tileEntity instanceof IAutoSentry)
		{
			if (!this.tileEntity.isValidTarget(this.tileEntity.getTarget()))
			{
				this.tileEntity.setTarget(null, true);
				this.tileEntity.cancelRotation();
				return false;
			}
			else if (this.tileEntity.canActivateWeapon())
			{
				this.tileEntity.onWeaponActivated();
			}
			else
			{
				float[] rotations = this.tileEntity.lookHelper.getDeltaRotations(this.tileEntity.getTargetPosition());
				this.tileEntity.rotateTo(rotations[0], rotations[1]);
			}
		}

		return true;
	}
}
