package atomicscience.api;

import net.minecraft.world.World;

public class Plasma
{
	/**
	 * Can be cast to block.
	 */
	public static IPlasma blockPlasma;

	public interface IPlasma
	{
		/**
		 * Spawns a plasma block at a specific location. Spawn strength is maxed out at 16, the
		 * metadata.
		 */
		public void spawn(World world, int x, int y, int z, byte strength);

		/**
		 * @return Can plasma be placed/exist in this specific position?
		 */
		public boolean canPlace(World world, int x, int y, int z);
	}
}
