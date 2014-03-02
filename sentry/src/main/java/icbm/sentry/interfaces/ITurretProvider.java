package icbm.sentry.interfaces;

import universalelectricity.api.vector.Vector3;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

/**
 * Implement this on any object that hosts a turret.
 * 
 * @author Darkguardsman, Calclavia
 */
public interface ITurretProvider
{
	/** Gets the sentry hosted by this container */
	public ITurret getTurret();

	public World world();

	public double x();

	public double y();

	public double z();

	public void sendFireEventToClient(Vector3 target);

	public IInventory getInventory();
}
