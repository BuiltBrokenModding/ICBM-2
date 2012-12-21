package atomicscience.api;

/**
 * This interface is applied to TileEntity that reacts to explosions in Atomic Science.
 * 
 * @author Calclavia
 * 
 */
public interface IExplosionReactor
{
	/**
	 * Called when this TileEntity receives an explosion impact
	 * 
	 * @param joules - The amount of energy released by the explosion.
	 */
	public void onExplode(double joules);
}
