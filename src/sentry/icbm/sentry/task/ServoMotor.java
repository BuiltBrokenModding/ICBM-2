package icbm.sentry.task;

import icbm.sentry.interfaces.IServo;

import com.builtbroken.common.Pair;

public class ServoMotor implements IServo
{
    private float rotation;
    private Pair<Float, Float> limits;

    public ServoMotor(float upperLimit, float lowerLimit)
    {
        this.limits = new Pair<Float, Float>(upperLimit, lowerLimit);
    }

    @Override
    public float getRotation()
    {
        return this.rotation;
    }

    @Override
    public void setRotation(float rotation)
    {
        this.rotation = rotation;
    }

    @Override
    public Pair<Float, Float> getLimits()
    {
        return this.limits;
    }

    @Override
    public void setLimits(float upperLimit, float lowerLimit)
    {
        this.limits = new Pair<Float, Float>(upperLimit, upperLimit);
    }

}
