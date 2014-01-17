package icbm.sentry.task;


public class TaskKillTarget extends TaskSearchTarget
{
    @Override
    protected boolean onUpdateTask()
    {
        super.onUpdateTask();

        if (!this.sentry().isValidTarget(this.sentry().getTarget()))
        {
            this.sentry().setTarget(null);
            this.sentry().cancelRotation();
            return false;
        }
        else if (this.sentry().lookHelper.canEntityBeSeen(this.sentry().getTarget()))
        {
            this.sentry().fireOn(this.sentry().getTarget());
        }
        else
        {
            float[] rotations = this.sentry().lookHelper.getDeltaRotations(this.sentry().getTargetPosition());
            this.sentry().rotateTo(rotations[0], rotations[1]);
        }

        return true;
    }
}
