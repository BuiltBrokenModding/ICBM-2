package icbm.sentry.turret.ai;

import net.minecraftforge.common.util.ForgeDirection;
import resonant.lib.transform.rotation.EulerAngle;

/** Automated version of the EulerAngle used by machines and entities to handle rotation
 * 
 * @author DarkGuardsman, Calclavia */
public class EulerServo extends EulerAngle
{
    public EulerAngle upperLimit = new EulerAngle(180, 40);
    public EulerAngle lowerLimit = new EulerAngle(-180, -40);
    private EulerAngle targetAngle = new EulerAngle(ForgeDirection.UNKNOWN);
    private double rotationSpeed;
    public boolean hasChanged = false;

    public EulerServo(double rotationSpeed)
    {
        super(ForgeDirection.UNKNOWN);
        this.rotationSpeed = rotationSpeed;
    }

    public EulerServo(EulerAngle angle, double rotationSpeed)
    {
        super(ForgeDirection.UNKNOWN);
        set(angle);
        this.rotationSpeed = rotationSpeed;
    }

    public EulerServo(double yaw, double pitch, double roll, double rotationSpeed)
    {
        super(yaw, pitch, roll);
        this.rotationSpeed = rotationSpeed;
    }

    public double getRotationSpeed()
    {
        return rotationSpeed;
    }

    public void setRotationSpeed(double speed)
    {
        this.rotationSpeed = speed;
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
            if (updateAngle(i))
            {
                this.hasChanged = true;
            }
        }
    }

    public boolean updateAngle(int index)
    {
        double[] array = toArray();
        double prevAngle = array[index];
        double currentAngle = array[index];
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
        return new EulerServo(this.yaw(), this.pitch(), this.roll(), this.getRotationSpeed());
    }
}
