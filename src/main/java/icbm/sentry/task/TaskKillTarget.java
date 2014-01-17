package icbm.sentry.task;

import icbm.sentry.interfaces.IAutoSentry;

public class TaskKillTarget extends TaskSearchTarget
{
    @Override
    protected boolean onUpdateTask()
    {
        super.onUpdateTask();

        if (this.sentry() instanceof IAutoSentry)
        {
            if (!this.sentry().isValidTarget(this.sentry().getTarget()))
            {
                this.sentry().setTarget(null);
                this.sentry().cancelRotation();
                return false;
            }
            else if (this.sentry().canActivateWeapon())
            {
                this.sentry().onWeaponActivated();
            }
            else
            {
                float[] rotations = this.sentry().lookHelper.getDeltaRotations(this.sentry().getTargetPosition());
                this.sentry().rotateTo(rotations[0], rotations[1]);
            }
        }

        return true;
    }
}
