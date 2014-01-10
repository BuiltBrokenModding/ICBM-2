package icbm.api;

import icbm.api.explosion.IExplosive;

import java.lang.reflect.Method;

/**
 * General ICBM references.
 * 
 * @author Calclavia
 * 
 */
public class ExplosiveHelper
{
	public static Class explosionManager;

	/**
	 * @return Gets an explosive object based on the name of the explosive.
	 */
	public static IExplosive getExplosive(String name)
	{
		if (name != null)
		{
			try
			{
				Method method = explosionManager.getMethod("get", String.class);
				return (IExplosive) method.invoke(null, name);
			}
			catch (Exception e)
			{
				System.out.println("ICBM: Failed to get explosive with the name: " + name);
				e.printStackTrace();
			}
		}

		return null;
	}
}
