package icbm.sentry.turret;

import icbm.sentry.turret.sentryhandler.Sentry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.utility.nbt.SaveManager;

/** @author Darkguardsman */
public class SentryRegistry
{
    private static HashMap<String, Class<? extends Sentry>> sentryMap = new HashMap<String, Class<? extends Sentry>>();
    
    public static void registerSentry (String key, Class<? extends Sentry> sentry)
    {
        synchronized (sentryMap)
        {
            if (!sentryMap.containsKey(key))
            {
                sentryMap.put(key, sentry);
                SaveManager.registerClass(key, sentry);
            }

        }
    }

    public static Class<? extends Sentry> get (String name)
    {
        return sentryMap.get(name);

    }

    public static HashMap<String, Class<? extends Sentry>> getMap ()
    {
        return sentryMap;
    }

    public static String getID (Sentry sentry)
    {
        for (Entry<String, Class<? extends Sentry>> entry : sentryMap.entrySet())
        {
            if (entry.getValue() != null && entry.getValue().equals(sentry.getClass()))
            {
                return entry.getKey();
            }
        }
        return null;
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
}
