package mffs.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract interface IForceFieldBlock
{
	public IProjector getProjector(IBlockAccess iBlockAccess, int x, int y, int z);

	public void weakenForceField(World world, int x, int y, int z);
}