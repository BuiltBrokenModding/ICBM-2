package atomicscience.api;

import net.minecraft.src.World;

public interface IAntiPoisonBlock
{
	/**
	 * Returns true if this armor prevents poison
	 * from the player
	 * 
	 * @return
	 */
	public boolean isPoisonPrevention(World par1World, int x, int y, int z, Poison type);
}
