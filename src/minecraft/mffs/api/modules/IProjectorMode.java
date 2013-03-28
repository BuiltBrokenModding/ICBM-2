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
	 * @param blockExterior - The blocks actually making up the force field
	 * @param blockInterior - The interior blocks within the force field.
	 */
	public void calculateField(IProjector projector, Set<Vector3> blockExterior, Set<Vector3> blockInterior);

	/**
	 * Called to render an object in front of the projection.
	 */
	public void render(IProjector projector, double x, double y, double z, float f, long ticks);
}
