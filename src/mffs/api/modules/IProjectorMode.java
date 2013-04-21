package mffs.api.modules;

import java.util.Set;

import mffs.api.IProjector;
import universalelectricity.core.vector.Vector3;

public interface IProjectorMode
{
	/**
	 * Called when the force field projector calculates the shape of the module.
	 * 
	 * @param projector - The Projector Object. Can cast to TileEntity.
	 * @param forceField - The blocks actually making up the force field. This array of blocks are
	 * NOT affected by rotation or translation.
	 */
	public void calculateField(IProjector projector, Set<Vector3> forceField);

	/**
	 * @return Is this specific position inside of this force field?
	 */
	public boolean isInField(IProjector projector, Vector3 position);

	/**
	 * Called to render an object in front of the projection.
	 */
	public void render(IProjector projector, double x, double y, double z, float f, long ticks);
}
