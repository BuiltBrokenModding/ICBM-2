package icbm.gangshao.actions;

import net.minecraft.nbt.NBTTagCompound;

public class ActionIdle extends Action
{
	/** The amount of time in which the machine will idle. */
	public int idleTime = 20;
	private int totalIdleTime = 20;

	@Override
	public void onTaskStart()
	{
		super.onTaskStart();

		if (this.getIntArg(0) > 0)
		{
			this.idleTime = this.getIntArg(0);
			this.totalIdleTime = this.idleTime;
		}
	}

	@Override
	protected boolean onUpdateTask()
	{
		if (this.idleTime > 0)
		{
			this.idleTime--;
			return true;
		}

		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.idleTime = taskCompound.getInteger("idleTime");
		this.totalIdleTime = taskCompound.getInteger("idleTotal");
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		taskCompound.setInteger("idleTime", this.idleTime);
		taskCompound.setInteger("idleTotal", this.totalIdleTime);
	}

}
