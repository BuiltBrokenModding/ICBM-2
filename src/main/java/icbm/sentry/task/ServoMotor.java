package icbm.sentry.task;

import icbm.api.sentry.IServo;


public class ServoMotor implements IServo
{
    private float rotation;
    private float lowerLimit;
    private float upperLimit;

    public ServoMotor(float upperLimit, float lowerLimit)
    {
        this.setLimits(upperLimit, lowerLimit);
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
    public void setLimits(float upperLimit, float lowerLimit)
    {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    @Override
    public float lowerLimit()
    {
        return this.lowerLimit;
    }

    @Override
    public float upperLimit()
    {
        return this.upperLimit;
    }

}
