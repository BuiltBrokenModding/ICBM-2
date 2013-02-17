package icbm.sentry.logic.actions;

import icbm.sentry.turret.TileEntityBaseTurret;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author Calclavia
 */
public class ActionRotateTo extends Action
{
	float targetRotationYaw = 0;
	float targetRotationPitch = 0;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();

		if (this.getArg(0) != null)
		{
			this.targetRotationYaw = this.getFloatArg(0);
		}
		else
		{
			this.targetRotationYaw = 0;
		}

		if (this.getArg(1) != null)
		{
			this.targetRotationPitch = this.getFloatArg(1);
		}
		else
		{
			this.targetRotationPitch = 0;
		}

		while (this.targetRotationYaw < 0)
			this.targetRotationYaw += 360;
		while (this.targetRotationYaw > 360)
			this.targetRotationYaw -= 360;

		while (this.targetRotationPitch < -TileEntityBaseTurret.MAX_PITCH)
			this.targetRotationPitch += TileEntityBaseTurret.MAX_PITCH;
		while (this.targetRotationPitch > TileEntityBaseTurret.MAX_PITCH)
			this.targetRotationPitch -= TileEntityBaseTurret.MAX_PITCH;
	}

	@Override
	protected boolean onUpdateTask()
	{
		super.onUpdateTask();

		this.tileEntity.targetRotationYaw = this.targetRotationYaw;
		this.tileEntity.targetRotationPitch = this.targetRotationPitch;

		if (Math.abs(this.tileEntity.rotationPitch - this.tileEntity.targetRotationPitch) > 0.001f)
		{
			return true;
		}
		if (Math.abs(this.tileEntity.rotationYaw - this.tileEntity.targetRotationYaw) > 0.001f)
		{
			return true;
		}

		return false;
	}

	@Override
	public String toString()
	{
		return "ROTATETO " + Float.toString(this.targetRotationYaw) + " " + Float.toString(this.targetRotationPitch);
	}

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.targetRotationPitch = taskCompound.getFloat("rotPitch");
		this.targetRotationYaw = taskCompound.getFloat("rotYaw");
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		taskCompound.setFloat("rotPitch", this.targetRotationPitch);
		taskCompound.setFloat("rotYaw", this.targetRotationYaw);
	}
}
