package icbm.api;

/**
 * This is an interface applied by all missile entities. You may cast this into an @Entity. The
 * "set" verison of the function will make the entity do the action on the next tick.
 * 
 * @author Calclavia
 */
public interface IMissile
{
	/**
	 * Blows up this missile. It will detonate the missile with the appropriate explosion.
	 */
	public void explode();

	public void setExplode();

	/**
	 * Blows up this missile like a TNT explosion. Small explosion used for events such as a missile
	 * crashing or failure to explode will result in this function being called.
	 */
	public void normalExplode();

	public void setNormalExplode();

	/**
	 * Drops the specified missile as an item.
	 */
	public void dropMissileAsItem();
}
