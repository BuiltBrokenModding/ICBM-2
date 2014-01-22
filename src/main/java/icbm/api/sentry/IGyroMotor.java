package icbm.api.sentry;

/** Applied to devices that have both a yaw and pitch rotation.
 * 
 * @author Darkguardsman */
public interface IGyroMotor
{
    /** Gets the servo instance that deals with yaw axis */
    IServo getYawServo();

    /** Gets the servo instance that deals with pitch axis */
    IServo getPitchServo();
}
