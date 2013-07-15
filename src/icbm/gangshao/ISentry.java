package icbm.gangshao;

import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.vector.Vector3;

/**
 * Applied to all turret TileEntities.
 */
public interface ISentry
{
	/**
	 * Set the sentry guns new rotation. This will be updated over time.
	 */
	public void setRotation(float yaw, float pitch);

	/**
	 * Gets the offset of the barrel from its actual location. Needs to be based off or current
	 * rotation of barrel for best results
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
	 * 
	 * @return The name of the turret.
	 */
	public String getName();
}
