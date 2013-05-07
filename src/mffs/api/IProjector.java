package mffs.api;

import java.util.Set;

import mffs.api.modules.IModule;
import mffs.api.modules.IModuleAcceptor;
import mffs.api.modules.IProjectorMode;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRotatable;

/**
 * Also extends IDisableable, IFortronFrequency
 * 
 * @author Calclavia
 * 
 */
public abstract interface IProjector extends IInventory, IModuleAcceptor, IRotatable, IBiometricIdentifierLink
{

	/**
	 * @return Is the projector active?
	 */
	public boolean isActive();

	/**
	 * Gets the mode of the projector, mainly the shape and size of it.
	 */
	public IProjectorMode getMode();

	public ItemStack getModeStack();

	/**
	 * Transformation information functions. Returns CACHED information unless the cache is cleared.
	 */
	public Vector3 getTranslation();

	public Vector3 getPositiveScale();

	public Vector3 getNegativeScale();

	public int getRotationYaw();

	public int getRotationPitch();

	/**
	 * Projects a force field.
	 */
	public void projectField();

	/**
	 * Destroys a force field.
	 */
	public void destroyField();

	/**
	 * Gets the unspecified, direction-unspecific module slots on the left side of the GUI.
	 */
	public int[] getModuleSlots();

	/**
	 * @param module - The module instance.
	 * @param direction - The direction facing.
	 * @return Gets the amount of modules based on the side.
	 */
	public int getSidedModuleCount(IModule module, ForgeDirection... direction);

	/**
	 * Gets the slot IDs based on the direction given.
	 */
	public int[] getSlotsBasedOnDirection(ForgeDirection direction);

	/**
	 * * @return Gets all the blocks that are occupying the force field.
	 */
	public Set<Vector3> getCalculatedField();

	/**
	 * @return The speed in which a force field is constructed.
	 */
	public int getProjectionSpeed();

	/**
	 * Gets the interior points of the projector. This might cause lag so call sparingly.
	 * 
	 * @return
	 */
	public Set<Vector3> getInteriorPoints();

	/**
	 * * @return The amount of ticks this projector has existed in the world.
	 */
	public long getTicks();

}