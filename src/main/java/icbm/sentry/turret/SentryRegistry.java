package icbm.sentry.turret;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.utility.nbt.SaveManager;

/** This is the main registry for all sentry object. Sentries should be registered using string names
 * to prevent conflict. This class will later be moved to the API so that other mods can generate
 * sentries.
 * 
 * @author Darkguardsman */
public class SentryRegistry
{
    private static HashMap<String, Class<? extends Sentry>> sentryMap = new HashMap<String, Class<? extends Sentry>>();

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
