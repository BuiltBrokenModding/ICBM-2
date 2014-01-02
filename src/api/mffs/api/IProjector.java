package mffs.api;

import java.util.Set;

import net.minecraft.inventory.IInventory;
import universalelectricity.api.vector.Vector3;

/**
 * Also extends IDisableable, IFortronFrequency
 * 
 * @author Calclavia
 * 
 */
public abstract interface IProjector extends IInventory, IBiometricIdentifierLink, IFieldInteraction
{
	/**
	 * Projects a force field.
	 */
	public void projectField();

	/**
	 * Destroys a force field.
	 */
	public void destroyField();

	/**
	 * @return The speed in which a force field is constructed.
	 */
	public int getProjectionSpeed();

	/**
	 * * @return The amount of ticks this projector has existed in the world.
	 */
	public long getTicks();

	/**
	 * DO NOT modify this list. Read-only.
	 * @return The actual force field block coordinates in the world.
	 */
	public Set<Vector3> getForceFields();

}