package icbm.api.flag;

import java.util.ArrayList;
import java.util.List;

/**
 * All the different types of flags that can be registered.
 * 
 * @author Calclavia
 * 
 */
public class FlagRegistry
{

	public static final List<String> flags = new ArrayList<String>();
	public static boolean isInitiated = false;

	/**
	 * Registers a flag name, allowing it to be used by the player. Call this in your mod's init
	 * function.
	 * 
	 * @return True on success of registry.
	 */
	public static boolean registerFlag(String name)
	{
		if (!isInitiated)
		{
			isInitiated = true;
		}

		if (!flags.contains(name))
		{
			flags.add(name);
			return true;
		}

		return false;
	}
}
