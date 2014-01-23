package icbm.api.sentry;

/** Very basic interface to deal with single axis of rotation. Normally this will be used in
 * combination to express yaw and pitch of a device.
 * 
 * @author Darkguardsman */
public interface IServo
{
    /** Gets the current rotation */
    float getRotation();

    /** sets the current rotation */
    void setRotation(float rotation);

    /** Lowest rotation that the servo can be at */
    float lowerLimit();

    /** Highest rotation that the servo can be atO */
    float upperLimit();

    /** Sets both the upper and lower limit of the servo */
    void setLimits(float upperLimit, float lowerLimit);
    
}
