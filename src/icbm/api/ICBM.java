package icbm.api;

import icbm.api.explosion.IExplosive;

import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * General ICBM references.
 * 
 * @author Calclavia
 * 
 */
public class ICBM
{
	/**
	 * Name of the channel and mod ID.
	 */
	public static final String NAME = "ICBM";

	/**
	 * The version of ICBM.
	 */
	public static final String MAJOR_VERSION = "@MAJOR@";
	public static final String MINOR_VERSION = "@MINOR@";
	public static final String REVISION_VERSION = "@REVIS@";
	public static final String BUILD_VERSION = "@BUILD@";
	public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION;

	/**
	 * The block ID in which ICBM starts with. ICBM Explosion will count up, ICBM Contraption will
	 * count down.
	 */
	public static final int BLOCK_ID_PREFIX = 3880;

	/**
	 * The item ID in which ICBM starts with.
	 */
	public static final int ITEM_ID_PREFIX = 3900;

	public static Class explosionManager;

	/**
	 * Created an ICBM explosion.
	 * 
	 * @param entity - The entity that created this explosion. The explosion source.
	 * @param explosiveID - The ID of the explosive.
	 */
	public static void createExplosion(World worldObj, double x, double y, double z, Entity entity, int explosiveID)
	{
		try
		{
			Method method = explosionManager.getMethod("createExplosion", World.class, Double.class, Double.class, Double.class, Entity.class, Integer.class);
			method.invoke(null, worldObj, x, y, z, entity, explosiveID);
		}
		catch (Exception e)
		{
			System.out.println("ICBM: Failed to create an ICBM explosion with the ID: " + explosiveID);
			e.printStackTrace();
		}
	}

	/**
	 * @return Gets an explosive object based on the name of the explosive.
	 */
	public static IExplosive getExplosive(String name)
	{
		if (name != null)
		{
			try
			{
				Method method = explosionManager.getMethod("getExplosiveByName", String.class);
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
