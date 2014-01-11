package icbm.api.sentry;

import icbm.sentry.task.IServo;

/**
 * @author Calclavia
 * 
 */
public interface IGyroMotor
{

	/**
	 * @return
	 */
	IServo getYawServo();

	/**
	 * @return
	 */
	IServo getPitchServo();

}
