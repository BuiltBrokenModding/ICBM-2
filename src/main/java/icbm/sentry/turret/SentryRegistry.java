package icbm.sentry.turret;

import icbm.sentry.turret.sentryhandler.Sentry;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import scala.tools.nsc.transform.Constructors;
import calclavia.lib.utility.nbt.SaveManager;

/** @author Darkguardsman, tgame14 */
public class SentryRegistry
{
    private static HashMap<Integer, Class<? extends Sentry>> sentryMap = new HashMap<Integer, Class<? extends Sentry>>();

    public static void registerSentry (Integer key, Class<? extends Sentry> sentry)
    {
        synchronized (sentryMap)
        {
            if (!sentryMap.containsKey(key))
            {
                sentryMap.put(key, sentry);
            }

        }
    }

    public static HashMap<Integer, Class<? extends Sentry>> getSentryMap ()
    {
        return sentryMap;
    }

    public static Class<? extends Sentry> getSentryForKey (String key)
    {
        return sentryMap.get(key);
    }

    public static Sentry build (NBTTagCompound compoundTag)
    {
        Object object = SaveManager.createAndLoad(compoundTag);
        if (object instanceof Sentry)
        {
            return (Sentry) object;
        }
        return null;
    }

    public static Sentry create (Integer key, Object... Args)
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
