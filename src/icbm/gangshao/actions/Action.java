package icbm.gangshao.actions;

import icbm.gangshao.turret.TileEntityTurretBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** An AI Commands that is used by TileEntities with AI.
 * 
 * @author Calclavia */
public abstract class Action
{
	/** The amount of ticks this command has been running for. */
	protected int ticks = 0;

	public World world;
	public TileEntityTurretBase tileEntity;
	public ActionManager commandManager;

	/** The parameters this command has, or the properties. Entered by the player in the disk.
	 * Parameters are entered like a Java function. idle(20) = Idles for 20 seconds. */
	private String[] parameters;

	/** Called by the TaskManager to propagate tick updates
	 * 
	 * @param ticks The amount of ticks this task has been running
	 * @return false if the task is finished, true otherwise */
	protected boolean onUpdateTask()
	{
		this.ticks++;
		return false;
	}

	public void onTaskStart()
	{
	}

	public void onTaskEnd()
	{
	}

	public boolean shouldExecute()
	{
		return true;
	}

	public void setParameters(String[] strings)
	{
		this.parameters = strings;
	}

	public String[] getArgs()
	{
		return this.parameters;
	}

	/** Some functions to help get parameter arguments. */
	protected String getArg(int i)
	{
		if (i >= 0 && i < this.parameters.length)
		{
			return this.parameters[i];
		}

		return null;
	}

	protected int getIntArg(int i)
	{
		if (getArg(i) != null)
		{
			try
			{
				return Integer.parseInt(getArg(i));
			}
			catch (Exception e)
			{

			}
		}

		return 0;
	}

	protected Double getDoubleArg(int i)
	{
		if (getArg(i) != null)
		{
			try
			{
				return Double.parseDouble(getArg(i));
			}
			catch (Exception e)
			{

			}
		}

		return 0.0;
	}

	protected Float getFloatArg(int i)
	{
		if (getArg(i) != null)
		{
			try
			{
				return Float.parseFloat(getArg(i));
			}
			catch (Exception e)
			{

			}
		}

		return 0.0f;
	}

	/** @return The tick interval of this task. 0 means it will receive no update ticks. */
	public int getTickInterval()
	{
		return 1;
	}

	public void readFromNBT(NBTTagCompound taskCompound)
	{
		this.ticks = taskCompound.getInteger("ticks");
	}

	public void writeToNBT(NBTTagCompound taskCompound)
	{
		taskCompound.setInteger("ticks", this.ticks);
	}
}
