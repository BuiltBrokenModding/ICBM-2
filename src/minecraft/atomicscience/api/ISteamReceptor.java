package atomicscience.api;

/**
 * Applied to TileEntities that can receive steam from below.
 */
public interface ISteamReceptor
{
	public void onReceiveSteam(int amount);
}
