package icbm.gangshao.actions;

import net.minecraft.nbt.NBTTagCompound;

/** Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author Calclavia */
public class ActionRotateBy extends Action
{
	float targetRotationYaw = 0;
	float targetRotationPitch = 0;
	float deltaPitch = 0, deltaYaw = 90;
	float totalTicks = 0f;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();

		this.ticks = 0;

		if (this.getArg(0) != null)
		{
			this.targetRotationYaw = this.tileEntity.wantedRotationYaw + this.getFloatArg(0);
			this.deltaYaw = this.getFloatArg(0);
		}
		else
		{
			this.targetRotationYaw = this.tileEntity.wantedRotationYaw + 90;
		}

		if (this.getArg(1) != null)
		{
			this.targetRotationPitch = this.tileEntity.wantedRotationPitch + this.getFloatArg(1);
			this.deltaPitch = this.getFloatArg(1);
		}
		else
		{
			this.targetRotationPitch = this.tileEntity.wantedRotationPitch;
		}

		while (this.targetRotationYaw < 0)
			this.targetRotationYaw += 360;
		while (this.targetRotationYaw > 360)
			this.targetRotationYaw -= 360;
		while (this.targetRotationPitch < 0)
			this.targetRotationPitch += 60;
		while (this.targetRotationPitch > 60)
			this.targetRotationPitch -= 60;

		float totalTicksYaw = Math.abs(this.targetRotationYaw - this.tileEntity.wantedRotationYaw) / this.tileEntity.getRotationSpeed();
		float totalTicksPitch = Math.abs(this.targetRotationPitch - this.tileEntity.wantedRotationPitch) / this.tileEntity.getRotationSpeed();
		this.totalTicks = Math.max(totalTicksYaw, totalTicksPitch);
	}

	@Override
	protected boolean onUpdateTask()
	{
		super.onUpdateTask();
		/*
		 * float rotationalDifference = Math.abs(this.tileEntity.rotationYaw - this.targetRotation);
		 * 
		 * if (rotationalDifference < ROTATION_SPEED) { this.tileEntity.rotationYaw =
		 * this.targetRotation; } else { if (this.tileEntity.rotationYaw > this.targetRotation) {
		 * this.tileEntity.rotationYaw -= ROTATION_SPEED; } else { this.tileEntity.rotationYaw +=
		 * ROTATION_SPEED; } this.ticks = 0; }
		 */

		// set the rotation to the target immediately and let the client handle animating it
		// wait for the client to catch up

		if (Math.abs(this.tileEntity.wantedRotationYaw - this.targetRotationYaw) > 0.001f)
			this.tileEntity.wantedRotationYaw = this.targetRotationYaw;
		if (Math.abs(this.tileEntity.wantedRotationPitch - this.targetRotationPitch) > 0.001f)
			this.tileEntity.wantedRotationPitch = this.targetRotationPitch;

		// if (this.ticks < this.totalTicks) { return true; }
		if (Math.abs(this.tileEntity.currentRotationYaw - this.tileEntity.wantedRotationPitch) > 0.001f)
		{
			return true;
		}
		if (Math.abs(this.tileEntity.currentRotationPitch - this.tileEntity.wantedRotationYaw) > 0.001f)
		{
			return true;
		}

		return false;
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

	@Override
	public String toString()
	{
		return "ROTATE " + Float.toString(this.deltaYaw) + " " + Float.toString(this.deltaPitch);
	}
}
