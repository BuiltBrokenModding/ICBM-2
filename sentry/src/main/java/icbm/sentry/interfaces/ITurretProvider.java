package icbm.sentry.interfaces;

import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.IGyroMotor;

/**
 * Used to interact with any container object that can host a sentry
 * 
 * @author Darkguardsman
 */
public interface ITurretProvider extends IGyroMotor
{
	/** Gets the sentry hosted by this container */
	public ITurret getTurret();

	/** Called to send the fire event to the client for rendering & sound */
	public void sendFireEventToClient(Vector3 target);

	public float yaw();

	public float pitch();

	/**
	 * These method should call to the Entity or TileEntity existing data.
	 * For TileEntity try to center the x y z coords
	 */
	public World world();

	public double x();

	public double y();

	public double z();

}
