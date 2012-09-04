package atomicscience.api;

/**
 * Apply this to all item armors and it will prevent the player from receiving a specific type of poison.
 */
public interface IAntiPoisonArmor
{
	/**
	 * Returns true if this armor prevents poison from the player
	 * @return
	 */
	public boolean isProtectedFromPoison(Poison type);
}
