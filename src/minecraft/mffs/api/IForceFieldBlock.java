package mffs.api;

import net.minecraft.world.World;

public abstract interface IForceFieldBlock
{
	public abstract void weakenForceField(World paramWorld, int paramInt1, int paramInt2, int paramInt3);
}