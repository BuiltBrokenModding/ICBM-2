package icbm.gangshao.actions;

import icbm.gangshao.turret.TileEntityTurretBase;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLLog;

public class ActionManager
{
	private final List<Action> tasks = new ArrayList<Action>();

	private int currentTask = 0;
	private int lastTask = -1;

	/** Must be called every tick by a tileEntity. */
	public void onUpdate()
	{
		/** Loop through each task and does them. */
		try
		{
			if (this.tasks.size() > 0)
			{
				if (this.currentTask < this.tasks.size())
				{
					if (this.currentTask < 0)
					{
						this.currentTask = 0;
						this.lastTask = -1;
					}

					Action task = this.tasks.get(this.currentTask);

					if (this.currentTask != this.lastTask)
					{
						this.lastTask = this.currentTask;
						task.onTaskStart();
					}

					if (!task.onUpdateTask())
					{
						int tempCurrentTask = this.currentTask;
						task.onTaskEnd();
						this.currentTask++;

						// Repeat needs to be persistent
						if (!(task instanceof ActionRepeat))
						{
							// End the task and reinitialize it into a new class to make sure it is
							// fresh.
							this.tasks.set(tempCurrentTask, this.getNewCommand(task.tileEntity, task.getClass(), task.getArgs()));
						}
					}
				}
				else
				{
					this.clear();
				}
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to execute task in Assembly Line.");
			e.printStackTrace();
		}
	}

	public Action getNewCommand(TileEntityTurretBase tileEntity, Class<? extends Action> commandClass, String[] parameters)
	{
		try
		{
			Action newCommand = commandClass.newInstance();
			newCommand.world = tileEntity.worldObj;
			newCommand.tileEntity = tileEntity;
			newCommand.commandManager = this;
			newCommand.setParameters(parameters);
			return newCommand;
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to add command");
			e.printStackTrace();
		}

		return null;
	}

	/** Used to register Tasks for a TileEntity, executes onTaskStart for the Task after registering
	 * it
	 * 
	 * @param tileEntity TE instance to register the task for
	 * @param newCommand Task instance to register */
	public void addCommand(TileEntityTurretBase tileEntity, Class<? extends Action> commandClass, String[] parameters)
	{
		Action newCommand = this.getNewCommand(tileEntity, commandClass, parameters);

		if (newCommand != null)
		{
			this.tasks.add(newCommand);
		}
	}

	public void addCommand(TileEntityTurretBase tileEntity, Class<? extends Action> task)
	{
		this.addCommand(tileEntity, task, new String[0]);
	}

	/** @return true when there are tasks registered, false otherwise */
	public boolean hasTasks()
	{
		return tasks.size() > 0;
	}

	public List<Action> getCommands()
	{
		return tasks;
	}

	/** Resets the command manager. */
	public void clear()
	{
		this.tasks.clear();
		this.currentTask = 0;
		this.lastTask = -1;
	}

	public void setCurrentTask(int i)
	{
		this.currentTask = Math.min(i, this.tasks.size());
	}

	public int getCurrentTask()
	{
		return this.currentTask;
	}
}
