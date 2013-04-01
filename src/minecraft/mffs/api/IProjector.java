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
public abstract interface IProjector extends IInventory, IModuleAcceptor, IRotatable
{

	public boolean isActive();

	/**
	 * Gets the mode of the projector, mainly the shape and size of it.
	 */
	public IProjectorMode getMode();

	public ItemStack getModeStack();

	public void projectField();

	public void destroyField();

	/**
	 * Gets the unspecified, direction-unspecific module slots on the left side of the GUI.
	 */
	public int[] getModuleSlots();

	public int getSidedModuleCount(IModule module, ForgeDirection... direction);

	/**
	 * Gets the slot IDs based on the direction given.
	 */
	public int[] getSlotsBasedOnDirection(ForgeDirection direction);

	/**
	 * The amount of fortron being used every tick.
	 * 
	 * @return
	 */
	public int getFortronCost();

	public Set<Vector3> getCalculatedField();

	public Set<Vector3> getInteriorPoints();

	/**
	 * The speed in which a force field is constructed.
	 * 
	 * @return
	 */
	public int getConstructionSpeed();

	ISecurityCenter getSecurityCenter();

}