package icbm.sentry.task;

import net.minecraft.nbt.NBTTagCompound;
import icbm.sentry.interfaces.IGyroMotor;
import icbm.sentry.interfaces.IServo;

import com.builtbroken.minecraft.helpers.MathHelper;
import com.builtbroken.minecraft.save.ISaveObj;

/** Modular way of dealing with yaw and pitch rotation of an object
 * 
 * @author DarkGuardsman */
public class RotationHelper implements ISaveObj
{
    protected IGyroMotor motor;
    protected float targetYaw, targetPitch;

    public RotationHelper(IGyroMotor motor)
    {
        this.motor = motor;
    }

    public void setTargetRotation(float yaw, float pitch)
    {
        this.targetYaw = yaw;
        this.targetPitch = pitch;
    }

    public void updateRotation(float speed)
    {
        this.updateRotation(speed, speed);
    }

    public void updateRotation(float speedYaw, float speedPitch)
    {
        //Clamp target angles
        this.targetYaw = (int) MathHelper.clampAngleTo360(this.targetYaw);
        this.targetPitch = (int) MathHelper.clampAngleTo360(this.targetPitch);
        updateRotation(this.motor.getYawServo(), speedYaw, this.targetYaw);
        updateRotation(this.motor.getPitchServo(), speedYaw, this.targetPitch);
    }

    /** Updates the rotation of a servo
     * 
     * @param servo - server to be rotated
     * @param speed - limit of how much to rotate in this update
     * @param targetRotation - target rotation */
    public static void updateRotation(IServo servo, float speed, float targetRotation)
    {
        if (servo != null)
        {
            float actualYaw = servo.getRotation();
            if (Math.abs(actualYaw - targetRotation) > speed)
            {
                if (Math.abs(actualYaw - targetRotation) >= 180)
                {
                    actualYaw += actualYaw > targetRotation ? speed : -speed;
                }
                else
                {
                    actualYaw += actualYaw > targetRotation ? -speed : speed;
                }
            }
            else
            {
                actualYaw = targetRotation;
            }
            actualYaw = MathHelper.clampAngleTo360(actualYaw);
            if (actualYaw > servo.getLimits().left())
            {
                actualYaw = servo.getLimits().left();
            }
            else if (actualYaw < servo.getLimits().right())
            {
                actualYaw = servo.getLimits().right();
            }
            servo.setRotation(actualYaw);
        }
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        nbt.setFloat("targetYaw", this.targetYaw);
        nbt.setFloat("targetPitch", this.targetPitch);

    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        this.targetYaw = nbt.getFloat("targetYaw");
        this.targetPitch = nbt.getFloat("targetPitch");
    }
}
