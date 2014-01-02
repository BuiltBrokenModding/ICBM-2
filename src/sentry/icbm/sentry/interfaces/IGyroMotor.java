package icbm.sentry.interfaces;

/** Simple interface to allow control of yaw and pitch in a device
 * 
 * @author DarkGuardsman */
public interface IGyroMotor
{
    /** Servo that control that yaw position of the device */
    public IServo getYawServo();

    /** Servo that control that pitch position of the device */
    public IServo getPitchServo();
}
