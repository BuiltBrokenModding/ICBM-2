package icbm.api.sentry;


/** @author Calclavia */
public interface IServo
{

    /** @return */
    float getRotation();

    /** @param rotation */
    void setRotation(float rotation);
    
    float lowerLimit();
    
    float upperLimit();

    /** @param upperLimit
     * @param lowerLimit */
    void setLimits(float upperLimit, float lowerLimit);

}
