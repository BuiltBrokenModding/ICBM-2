package icbm.api.sentry;

import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.vector.Vector3;

/**
 * Applied to all turret TileEntities.
 * 
 * @author Calclavia
 * 
 */
public interface ISentry
{
	/**
	 * Set the sentry guns new rotation. This will be updated over time.
	 */
	public void setRotation(float yaw, float pitch);

	/**
	 * Gets the offset of the barrel from its actual location
	 */
	public Vector3 getMuzzle();

	/**
	 * Activates the weapon. In most cases, this will cause the turret to fire.
	 */
	public void onWeaponActivated();

	/**
	 * called to check that the sentries weapon can be fired
	 */
	public boolean canActivateWeapon();

	/**
	 * Gets the turret platform.
	 */
	public TileEntity getPlatform();

	/**
	 * The amount of time it takes to cool down per fire.
	 * 
	 * @return
	 */
	public int getCooldown();

	/**
	 * The amount of joules required for the turret to run.
	 * 
	 * @return
	 */
	public double getRequest();

	/**
	 * 
	 * @return The name of the turret.
	 */
	public String getName();
}
