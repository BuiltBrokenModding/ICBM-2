package icbm.api.sentry;


/** @author Calclavia */
public interface IGyroMotor
{

    /** @return */
    IServo getYawServo();

    /** @return */
    IServo getPitchServo();

}
