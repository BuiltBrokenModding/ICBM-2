package icbm.api;

/**
 * This is an interface applied by all missile entities. You may cast this into {@Entity}.
 * @author Calclavia
 */
public interface IMissile
{
	/**
	 * Blows up this missile depending on it's type.
	 */
	public void explode();
	
	/**
	 * Blows up this missile like a TNT explosion. Small explosion used for events such as
	 * a missile crashing or failure to explode will result in this function being called.
	 */
	public void normalExplode();
	
	/**
	 * Drops the specified missile as an item.
	 */
	public void dropMissileAsItem();
}
