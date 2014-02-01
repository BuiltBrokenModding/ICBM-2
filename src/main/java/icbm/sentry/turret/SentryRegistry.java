package icbm.sentry.turret;

import calclavia.lib.utility.nbt.SaveManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.sentry.ICBMSentry;
import icbm.sentry.render.SentryRenderer;
import icbm.sentry.turret.block.TileSentry;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/** This is the main registry for all sentry object. Sentries should be registered using string names
 * to prevent conflict. This class will later be moved to the API so that other mods can generate
 * sentries.
 * 
 * @author Darkguardsman */
public class SentryRegistry
{
    private static HashMap<String, Class<? extends Sentry>> sentryMap = new HashMap<String, Class<? extends Sentry>>();

    @SideOnly(Side.CLIENT)
    private static HashMap<Class<? extends Sentry>, SentryRenderer> sentryRenderMap;

    // TODO Fix SaveManager to handle the Tile argument
    /** Registers a sentry to be handled by ICBM
     * 
     * @param key - ID the sentry goes by, should be unique, will be registered with save manager
     * prefixed by 'Sentry-'
     * @param sentry - class file that extends Sentry.class and has a no param constructor */
    public static void registerSentry(String key, Class<? extends Sentry> sentry)
    {
        synchronized (sentryMap)
        {
            if (!sentryMap.containsKey(key))
            {
                sentryMap.put(key, sentry);
                SaveManager.registerClass("Sentry-" + key, sentry);
            }
        }
    }

    /** Registers a renderer for a sentry in the same way a TileEntitySpecialRender is handled.
     * 
     * @param sentry - sentry class this renderer is for
     * @param render - renderer instance and is treated as static */
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
                sentryRenderMap.put(sentry, render);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static SentryRenderer getRenderFor(Sentry sentry)
    {
        if (sentry != null)
        {
            Class clazz = sentry.getClass();
            if (sentryRenderMap.containsKey(clazz))
            {
                return sentryRenderMap.get(sentry.getClass());
            }
            else
            {
                //TODO cycle threw list checking if clazz is applicable to X class
            }
        }
        return null;
    }

    /** Gets teh sentry map */
    public static HashMap<String, Class<? extends Sentry>> getSentryMap()
    {
        return sentryMap;
    }

    /** Grabs the class that links back the sentry id */
    public static Class<? extends Sentry> getSentryForKey(String key)
    {
        return sentryMap.get(key);
    }

    /** Builds a sentry from a save using the SaveManager
     * 
     * @param compoundTag - NBT save
     * @return new Sentry instance or null if it failed */
    public static Sentry build(NBTTagCompound compoundTag)
    {
        Object object = SaveManager.createAndLoad(compoundTag);
        if (object instanceof Sentry)
        {
            return (Sentry) object;
        }
        return null;
    }

    /**
     * @param id the key of the Sentry class used in SentryRegistry.registerSentry()
     * @param host the tile the Sentry is created for
     * @return the Sentry module for the given id and tile, or null if the sentry isn't registered or an error occured wen constructing
     */
    public static Sentry constructSentry(String id, TileSentry host)
    {
        Object candidate = null;
        Sentry sentryModule = null;

        try
        {
            Class<? extends Sentry> clazz = SaveManager.getClass(id);

            if (clazz == null)
            {
                ICBMSentry.LOGGER.severe("Attempted Sentry for construction is not registered");
                return null;
            }

            Constructor[] constructors = clazz.getConstructors();

                for (Constructor constructor : constructors)
                {
                    if (constructor.getParameterTypes().length == 1)
                    {
                        candidate = constructor.newInstance(host);
                        break;
                    }

                    if (candidate instanceof Sentry)
                        sentryModule = (Sentry) candidate;
                    else
                        ICBMSentry.LOGGER.severe("construction of Sentry failed, an unexpected Object was created");
                }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return sentryModule;


    }

    /** Marked for Removal, will be replaced with SentryRegistry.constructSentry() */
    @Deprecated
    public static Sentry create(Integer key, Object... Args)
    {
        Object obj = null;
        Sentry s = null;

        try
        {
            Class<? extends Sentry> clazz = sentryMap.get(key);

            Constructor<?>[] cons = clazz.getConstructors();
            for (Constructor con : cons)
            {
                if (con.getParameterTypes().length == Args.length)
                    obj = con.newInstance(Args);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (obj instanceof Sentry)
            s = (Sentry) obj;
        return s;
    }

}
