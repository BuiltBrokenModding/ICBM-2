package icbm.sentry.interfaces;


/** Simple interface to control an object that rotates
 * 
 * @author DarkGuardsman */
public interface IServo
{
    /** Gets the rotation */
    public float getRotation();

    /** Sets the rotation */
    public void setRotation(float rotation);

    /** Gets limits or rotation with left being upper, and right being lower */
    public Pair<Float, Float> getLimits();

    public void setLimits(float upperLimit, float lowerLimit);
}
