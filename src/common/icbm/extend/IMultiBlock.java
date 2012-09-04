package icbm.extend;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import universalelectricity.Vector3;

/**
 * Interface to be applied to tile entity blocks that occupies more than one block space. Useful for large machines.
 * @author Calclavia
 *
 */
public interface IMultiBlock
{
	/**
	 * Called when this multiblock is created
	 * @param placedPosition - The position the block was placed at
	 */
	public void onCreate(Vector3 placedPosition);
	
	/**
	 * Called when one of the multi blocks of this block is destroyed
	 * @param callingBlock - The tile entity who called the onDestroy function
	 */
	public void onDestroy(TileEntity callingBlock);
	
	/**
	 * Called when one of the multiblocks gets activated
	 */
	public boolean onActivated(EntityPlayer entityPlayer);
}
