package icbm.sentry.turret;

import icbm.api.sentry.IGyroMotor;
import icbm.api.sentry.IServo;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.network.IPacketLoad;
import calclavia.lib.utility.MathUtility;
import calclavia.lib.utility.nbt.ISaveObj;

import com.google.common.io.ByteArrayDataInput;

/** Modular way of dealing with yaw and pitch rotation of an object
 * 
 * @author DarkGuardsman */
public class RotationHelper implements ISaveObj, IPacketLoad
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
        // Clamp target angles
        this.targetYaw = (int) MathUtility.clampAngleTo360(this.targetYaw);
        this.targetPitch = (int) MathUtility.clampAngleTo360(this.targetPitch);
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
            actualYaw = MathUtility.clampAngleTo360(actualYaw);
            if (actualYaw > servo.upperLimit())
            {
                actualYaw = servo.upperLimit();
            }
            else if (actualYaw < servo.lowerLimit())
            {
                actualYaw = servo.lowerLimit();
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

    @Override
    public void readPacket(ByteArrayDataInput data)
    {
        this.targetYaw = data.readFloat();
        this.targetPitch = data.readFloat();
        this.motor.getYawServo().setRotation(data.readFloat());
        this.motor.getPitchServo().setRotation(data.readFloat());
    }

    @Override
    public void loadPacket(DataOutputStream data) throws IOException
    {
        data.writeFloat(this.targetYaw);
        data.writeFloat(this.targetPitch);
        data.writeFloat(this.motor.getYawServo().getRotation());
        data.writeFloat(this.motor.getPitchServo().getRotation());

    }
}
