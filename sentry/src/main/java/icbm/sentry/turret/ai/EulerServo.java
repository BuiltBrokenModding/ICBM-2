package icbm.sentry.turret.ai;

import universalelectricity.api.vector.EulerAngle;

public class EulerServo extends EulerAngle
{
    public EulerAngle upperLimit = new EulerAngle(180, 40);
    public EulerAngle lowerLimit = new EulerAngle(-180, -40);
    private EulerAngle targetAngle = new EulerAngle();
    private double rotationSpeed;
    public boolean hasChanged = false;

    public EulerServo(double rotationSpeed)
    {
        this(new EulerAngle(), rotationSpeed);
    }

    public EulerServo(EulerAngle angle, double rotationSpeed)
    {
        super(angle);
        this.rotationSpeed = rotationSpeed;
    }

    public EulerServo setLimit(EulerAngle upperLimit, EulerAngle lowerLimit)
    {
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
        return this;
    }

    public void update()
    {
        this.hasChanged = false;
        
        for (int i = 0; i < toArray().length; i++)
        {
            if(updateAngle(i))
            {
                this.hasChanged = true;
            }
        }
    }

    public boolean updateAngle(int index)
    {
        double prevAngle = toArray()[index];
        double currentAngle = toArray()[index];
        double targetAngle = this.targetAngle.toArray()[index];
        double upperLimit = this.upperLimit.toArray()[index];
        double lowerLimit = this.lowerLimit.toArray()[index];

        if (Math.abs(currentAngle - targetAngle) > rotationSpeed)
        {
            if (Math.abs(currentAngle - targetAngle) >= 180)
            {
                currentAngle += currentAngle > targetAngle ? rotationSpeed : -rotationSpeed;
            }
            else
            {
                currentAngle += currentAngle > targetAngle ? -rotationSpeed : rotationSpeed;
            }
        }
        else
        {
            currentAngle = targetAngle;
        }

        currentAngle = clampAngleTo180(currentAngle);

        if (currentAngle > upperLimit)
        {
            currentAngle = upperLimit;
        }
        else if (currentAngle < lowerLimit)
        {
            currentAngle = lowerLimit;
        }

        set(index, currentAngle);
        return currentAngle != prevAngle;
    }

    public void setTargetRotation(EulerAngle targetRotation)
    {
        this.targetAngle = targetRotation;
    }

    public boolean isWithinLimit(EulerAngle compare)
    {
        for (int i = 0; i < compare.toArray().length; i++)
            if (compare.toArray()[i] > upperLimit.toArray()[i] || compare.toArray()[i] < lowerLimit.toArray()[i])
                return false;

        return true;
    }

    @Override
    public EulerServo clone()
    {
        return new EulerServo(this, rotationSpeed);
    }
}
