package icbm.gangshao.task;

import icbm.core.ZhuYaoICBM;
import icbm.gangshao.turret.sentries.TPaoTaiZiDong;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
	private final List<Task> tasks = new ArrayList<Task>();

	public TPaoTaiZiDong tileEntity;

	public TaskManager(TPaoTaiZiDong tileEntity) {
		this.tileEntity = tileEntity;
	}

	/** Must be called every tick by a tileEntity. */
	public void onUpdate() {
		/** Loop through each task and does them. */
		try {
			if (this.tasks.size() > 0) {
				int taskIndex = 0;

				Task currentTask = this.tasks.get(taskIndex);

				if (currentTask != null) {
					if (!currentTask.onUpdateTask()) {
						currentTask.onTaskEnd();
						this.tasks.remove(taskIndex);
					}
				}
			}
		} catch (Exception e) {
			ZhuYaoICBM.LOGGER.severe("Failed to execute AI tasks!");
			e.printStackTrace();
		}
	}

	public void addTask(Task task) {
		task.taskManager = this;
		task.world = this.tileEntity.worldObj;
		task.tileEntity = this.tileEntity;
		this.tasks.add(task);
	}

	/** @return true when there are tasks registered, false otherwise */
	public boolean hasTasks() {
		return tasks.size() > 0;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	/** Resets the command manager. */
	public void clear() {
		this.tasks.clear();
	}

	public int getTaskCount() {
		return this.tasks.size();
	}
}
