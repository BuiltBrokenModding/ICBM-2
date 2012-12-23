package icbm.api;

/**
 * Implement this to your entity if you want antiballistic missiles to be able to lock onto it.
 * 
 * @author Calclavia
 * 
 */
public interface IMissileLockable
{
	public boolean canLock();
}
