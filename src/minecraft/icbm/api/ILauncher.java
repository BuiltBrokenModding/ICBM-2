package icbm.api;

import net.minecraft.item.ItemStack;
import universalelectricity.core.block.IElectricityStorage;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneReceptor;

/**
 * Applied to all launcher tile entities.
 * 
 * @author Calclavia
 */
public interface ILauncher extends IElectricityStorage, IRedstoneReceptor, IBlockFrequency
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
	 * @param target Sets the target of the launcher
	 */
	public void setTarget(Vector3 target);

	/**
	 * Places a missile into the launcher.
	 */
	void placeMissile(ItemStack itemStack);
}