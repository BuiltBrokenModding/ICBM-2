package icbm.gangshao.actions;

import icbm.gangshao.turret.TileEntityTurretBase;
import net.minecraft.nbt.NBTTagCompound;

/** Rotates the armbot to a specific direction. If not specified, it will turn right.
 * 
 * @author Calclavia */
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

		while (this.targetRotationPitch < TileEntityTurretBase.MIN_PITCH)
		{
			this.targetRotationPitch += TileEntityTurretBase.MIN_PITCH;
		}
		while (this.targetRotationPitch > TileEntityTurretBase.MAX_PITCH)
		{
			this.targetRotationPitch -= TileEntityTurretBase.MAX_PITCH;
		}
	}

	@Override
	protected boolean onUpdateTask()
	{
		super.onUpdateTask();

		this.tileEntity.wantedRotationYaw = this.targetRotationYaw;
		this.tileEntity.wantedRotationPitch = this.targetRotationPitch;

		if (Math.abs(this.tileEntity.currentRotationPitch - this.tileEntity.wantedRotationPitch) > 0.001f)
		{
			return true;
		}
		if (Math.abs(this.tileEntity.currentRotationYaw - this.tileEntity.wantedRotationYaw) > 0.001f)
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
