package mffs.api;

/**
 * Applied to TileEntities who would like to handle movement by the force manipulation in a better
 * way.
 * 
 * @author Calclavia
 * 
 */
public interface ISpecialForceManipulation
{
	/**
	 * Called before the TileEntity is moved. After this function is called, the TileEntity will be
	 * moved after 1 second exactly (due to the fact that there is an animation delay).
	 * 
	 * @param Coords - X, Y, Z (Target location to be moved)
	 * @return True if it can be moved.
	 */
	public boolean preMove(int x, int y, int z);

	/**
	 * Called right before the TileEntity is moved. After this function is called, the force
	 * manipulator will write all TileEntity data into NBT and remove the TileEntity block. A new
	 * TileEntity class will be instantiated after words in the new position.
	 * 
	 * @param Coords - X, Y, Z (Target location to be moved)
	 */
	public void move(int x, int y, int z);

	/**
	 * Called after the TileEntity is moved. The TileEntity will be given a
	 * notifyBlocksOfNeighborChange call before this is called.
	 */
	public void postMove();
}
