package icbm.sentry.task;

import com.builtbroken.common.Pair;

/** @author Calclavia */
public interface IServo
{

    /** @return */
    float getRotation();

    /** @param rotation */
    void setRotation(float rotation);

    /** @return */
    Pair<Float, Float> getLimits();

    /** @param upperLimit
     * @param lowerLimit */
    void setLimits(float upperLimit, float lowerLimit);

}
