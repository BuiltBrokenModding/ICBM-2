package icbm.api;

import net.minecraft.src.ItemStack;
import universalelectricity.core.implement.IJouleStorage;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneReceptor;

public class Launcher
{
	/**
	 * Types of launchers
	 * 
	 * @author Calclavia
	 * 
	 */
	public enum LauncherType
	{
		TRADITIONAL, CRUISE
	}

	/**
	 * Applied to all launcher tile entities.
	 * 
	 * @author Calclavia
	 */
	public interface ILauncher extends IJouleStorage, IRedstoneReceptor, IFrequency
	{
		/**
		 * What type of launcher is this?
		 */
		public LauncherType getLauncherType();

		/**
		 * Launches the missile into the specified target.
		 */
		public void launch();

		/**
		 * Can the launcher launch the missile?
		 */
		public boolean canLaunch();

		/**
		 * @return The status of the launcher.
		 */
		public String getStatus();

		/**
		 * @return The target of the launcher.
		 */
		public Vector3 getTarget();

		/**
		 * @param target
		 *            Sets the target of the launcher
		 */
		public void setTarget(Vector3 target);

		/**
		 * Places a missile into the launcher.
		 */
		void placeMissile(ItemStack itemStack);
	}

}