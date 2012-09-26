package icbm.extend;

import net.minecraft.src.EntityPlayer;

public interface IBlockActivate 
{
	/**
	 * Called when one of the multiblocks gets activated
	 */
	public boolean onActivated(EntityPlayer entityPlayer);
}
