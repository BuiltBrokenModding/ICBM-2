package icbm.sentry.turret;

/** Automated version of the servo that handles most rotation on its own. Only thing you have to do
 * is tell it the angle to rotate to.
 * 
 * @author Darkguardsman */
public class AutoServo extends Servo
{
    private float targetRotation = 0;
    private float speed = 1;

    public AutoServo(float upperLimit, float lowerLimit)
    {
        super(upperLimit, lowerLimit);
    }

    public AutoServo(float upperLimit, float lowerLimit, float speed)
    {
        super(upperLimit, lowerLimit);
        this.setSpeed(speed);
    }

    public void rotateTo(float angle)
    {
        this.targetRotation = angle;
    }

    public void update()
    {
        RotationHelper.updateRotation(this, speed, targetRotation);
    }

    public void setTargetRotation(float yaw)
    {
        this.targetRotation = yaw;
    }

    public void setSpeed(float speed)
    {
        this.speed = Math.abs(speed);
    }

    public float getSpeed()
    {
        return speed;
    }

}
