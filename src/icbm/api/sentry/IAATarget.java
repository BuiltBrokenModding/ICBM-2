package icbm.api.sentry;

/**
 * Apply this to an entity if it is meant to be targeted by the AA Turret.
 * 
 * @author Calclavia
 * 
 */
public interface IAATarget
{
	/**
	 * destroys the target with a boom
	 */
	public void explode();

	/**
	 * Applies damage to the the target
	 * 
	 * @param damage - damage in half HP
	 * @return the amount of HP left. Return -1 if this target can't take damage, and will be chance
	 * killed. Return 0 if this target is dead
	 */
	public int doDamage(int damage);
}
