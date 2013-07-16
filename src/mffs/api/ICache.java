package mffs.api;

/**
 * For objects that uses caching method to speed up process power.
 * 
 * @author Calclavia
 * 
 */
public interface ICache
{
	public Object getCache(String cacheID);

	public void clearCache(String cacheID);

	public void clearCache();
}
