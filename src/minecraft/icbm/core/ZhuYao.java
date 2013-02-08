package icbm.core;

/**
 * Main class for ICBM core to run on. Treat as a core mod that all other modules are dependent from
 * but remain packaged in each module for distribution.
 * 
 * @author Calclavia
 * 
 */

public class ZhuYao
{
	public static boolean isInitialized = false;

	public static void init()
	{
		if (!isInitialized)
		{

		}

		isInitialized = true;
	}
}
