package mffs.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract interface IForceFieldBlock
{
	public IProjector getProjector(IBlockAccess iBlockAccess, int x, int y, int z);

	/**
	 * Weakens a force field block, destroying it temporarily and draining power from the projector.
	 * 
	 * @param joules - Power to drain.
	 */
	public void weakenForceField(World world, int x, int y, int z, int joules);
}