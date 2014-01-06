package icbm.sentry.task;

import icbm.core.ICBMCore;
import icbm.sentry.turret.sentries.TileEntityAutoTurret;

import java.util.ArrayList;
import java.util.List;

public class TaskManager
{
    private final List<Task> tasks = new ArrayList<Task>();
    private int taskIndex = 0;
    protected TileEntityAutoTurret tileEntity;

    public TaskManager(TileEntityAutoTurret tileEntity)
    {
        this.tileEntity = tileEntity;
    }

    /** Must be called every tick by a tileEntity. */
    public void onUpdate()
    {
        try
        {
            if (this.tasks.size() > 0)
            {
                Task currentTask = this.tasks.get(taskIndex);

                if (currentTask != null)
                {
                    if (!currentTask.onUpdateTask())
                    {
                        currentTask.onTaskEnd();
                        if (this.taskIndex < this.tasks.size() - 1)
                            this.taskIndex++;
                        else
                            this.taskIndex = 0;
                    }
                }
            }
        }
        catch (Exception e)
        {
            ICBMCore.LOGGER.severe("[ICBM:Sentry]Failed to execute AI tasks!");
            e.printStackTrace();
        }
    }

    public void addTask(Task task)
    {
        task.taskManager = this;
        task.world = this.tileEntity.worldObj;
        task.tileEntity = this.tileEntity;
        this.tasks.add(task);
    }

    /** @return true when there are tasks registered, false otherwise */
    public boolean hasTasks()
    {
        return tasks.size() > 0;
    }

    public List<Task> getTasks()
    {
        return tasks;
    }

    /** Resets the command manager. */
    public void clear()
    {
        this.tasks.clear();
    }

    public int getTaskCount()
    {
        return this.tasks.size();
    }
}
