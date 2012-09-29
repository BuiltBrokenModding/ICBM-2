package icbm.extend;

import net.minecraft.src.Chunk;

/**
 * Applied to all objects that need to called when a chunk loads. If applied to a tile entity, it will be called
 * only if the chunk is the one the tile entity is on.
 * @author Calclavia
 *
 */
public interface IChunkLoadHandler
{
	/**
	 * Called every time when a chunk loads with this tile entity in it.
	 */
	public void onChunkLoad(Chunk chunk);
	
	/**
	 * Called every time when a chunk unloads with this tile entity in it.
	 */
	public void onChunkUnload(Chunk chunk);
}
