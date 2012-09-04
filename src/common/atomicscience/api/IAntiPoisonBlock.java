package atomicscience.api;

public interface IAntiPoisonBlock
{
	/**
	 * Returns true if this armor prevents poison from the player
	 * @return
	 */
	public boolean isPoisonPrevention(Poison type);
}
