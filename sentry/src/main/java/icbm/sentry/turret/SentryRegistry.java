package icbm.sentry.turret;

import icbm.core.ICBMCore;
import icbm.sentry.render.SentryRenderer;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.google.common.collect.HashBiMap;

import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.utility.ReflectionHelper;
import calclavia.lib.utility.nbt.SaveManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This is the main registry for all sentry object. Sentries should be registered using string names
 * to prevent conflict. This class will later be moved to the API so that other mods can generate
 * sentries.
 * 
 * @author Darkguardsman
 */
public class SentryRegistry
{
	private static HashBiMap<String, Class<? extends Sentry>> sentryMap = HashBiMap.create();

	@SideOnly(Side.CLIENT)
	private static HashMap<Class<? extends Sentry>, SentryRenderer> sentryRenderMap;

	// TODO Fix SaveManager to handle the Tile argument
	/**
	 * Registers a sentry to be handled by ICBM
	 * 
	 * @param key - ID the sentry goes by, should be unique, will be registered with save manager
	 * prefixed by 'Sentry-'
	 * @param sentry - class file that extends Sentry.class and has a no param constructor
	 */
	public static void registerSentry(String key, Class<? extends Sentry> sentry)
	{
		synchronized (sentryMap)
		{
			if (!sentryMap.containsKey(key))
			{
				sentryMap.put(key, sentry);
				SaveManager.registerClass("Sentry-" + key, sentry);
				System.out.println("[SentryRegistry]Registering Sentry . Sentry: " + sentry.getName() + "  ID: " + key + " Save-ID: Sentry-" + key);

			}
		}
	}

	/**
	 * Registers a renderer for a sentry in the same way a TileEntitySpecialRender is handled.
	 * 
	 * @param sentry - sentry class this renderer is for
	 * @param render - renderer instance and is treated as static
	 */
	@SideOnly(Side.CLIENT)
	public static void registerSentryRenderer(Class<? extends Sentry> sentry, SentryRenderer render)
	{
		if (sentryRenderMap == null)
		{
			sentryRenderMap = new HashMap<Class<? extends Sentry>, SentryRenderer>();
		}
		synchronized (sentryRenderMap)
		{
			if (!sentryRenderMap.containsKey(sentry))
			{
				System.out.println("[SentryRegistry]Registering Sentry Renderer. Sentry: " + sentry.getName() + "  Renderer: " + render.getClass().getName());
				sentryRenderMap.put(sentry, render);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static SentryRenderer getRenderFor(Sentry sentry)
	{
		if (sentry != null)
		{
			Class<?> clazz = sentry.getClass();
			if (sentryRenderMap.containsKey(clazz))
			{
				return sentryRenderMap.get(sentry.getClass());
			}
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	public static SentryRenderer getRenderFor(Class<? extends Sentry> sentry)
	{
		return sentryRenderMap.get(sentry);
	}

	/** Gets teh sentry map */
	public static HashBiMap<String, Class<? extends Sentry>> getSentryMap()
	{
		return sentryMap;
	}

	/** Grabs the class that links back the sentry id */
	public static Class<? extends Sentry> getSentryForKey(String key)
	{
		return sentryMap.get(key);
	}

	public static String getKeyForSentry(Sentry sentry)
	{
		return sentryMap.inverse().get(sentry.getClass());
	}

	/**
	 * Builds a sentry from a save using the SaveManager. This assumes that the sentries save ID was
	 * saved to the NBTTagCompound. If it was not then use the other construct method followed by
	 * calling the load method.
	 * 
	 * @param compoundTag - NBT save
	 * @param args - args that will be passed into the sentry's class constructor
	 * @return new Sentry instance or null if it failed
	 */
	public static Sentry constructSentry(NBTTagCompound compoundTag, Object... args)
	{
		Object object = SaveManager.createAndLoad(compoundTag, args);
		if (object instanceof Sentry)
		{
			return (Sentry) object;
		}
		return null;
	}

	/**
	 * Builds a new sentry instance from the given sentry ID and argument. Sentry ID must be
	 * registered to a class, and the args must match a constructor in the registered class.
	 * 
	 * @param sentryID the key of the Sentry class used in SentryRegistry.registerSentry()
	 * @param args arguments the Sentry's class constructor requires
	 * @return the Sentry object for the given id and tile, or null it failed to create the sentry
	 */
	public static Sentry constructSentry(String sentryID, Object... args)
	{
		Object candidate = null;
		Sentry sentryModule = null;

		try
		{
			Class clazz = SaveManager.getClass(sentryID);

			if (clazz != null)
			{
				Constructor<?> con = ReflectionHelper.getConstructorWithArgs(clazz, args);
				if (con != null)
				{
					candidate = con.newInstance(args);
				}
				if (candidate instanceof Sentry)
				{
					sentryModule = (Sentry) candidate;
				}
				else
				{
					ICBMCore.LOGGER.severe("Construction of Sentry failed, an unexpected Object was created");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return sentryModule;

	}

}
