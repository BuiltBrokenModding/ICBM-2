package icbm.gangshao.task;

import icbm.gangshao.turret.TPaoDaiBase;
import net.minecraft.world.World;

/**
 * An AI Commands that is used by TileEntities with AI.
 * 
 * @author Calclavia
 */
public abstract class Task
{
	/** The amount of ticks this command has been running for. */
	protected int ticks = 0;

	public World world;
	public TPaoDaiBase tileEntity;
	public TaskManager taskManager;

	/**
	 * Called by the TaskManager to propagate tick updates
	 * 
	 * @param ticks The amount of ticks this task has been running
	 * @return false if the task is finished, true otherwise
	 */
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

	/** @return The tick interval of this task. 0 means it will receive no update ticks. */
	public int getTickInterval()
	{
		return 1;
	}
}
