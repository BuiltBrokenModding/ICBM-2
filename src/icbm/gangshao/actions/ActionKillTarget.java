package icbm.gangshao.actions;

import java.awt.Color;

import universalelectricity.core.vector.Vector3;
import dark.library.DarkMain;
import icbm.api.IAutoSentry;

public class ActionKillTarget extends ActionSearchTarget
{
	int ticksTarget = 0;/* ticks since target has been seen */

	@Override
	protected boolean onUpdateTask()
	{
		super.onUpdateTask();

		if (this.tileEntity instanceof IAutoSentry)
		{
			IAutoSentry turret = (IAutoSentry) this.tileEntity;

			if (!turret.isValidTarget(turret.getTarget()))
			{
				if (this.ticksTarget++ < 10)
				{
					return true;
				}
				else
				{
					turret.setTarget(null, true);
					return false;
				}
			}
			else if (turret.canActivateWeapon())
			{				
				turret.onWeaponActivated();
			}
		}
		return false;
	}
}
