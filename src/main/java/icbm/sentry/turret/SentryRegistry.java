package icbm.sentry.turret;

import icbm.sentry.turret.sentryHandlers.Sentry;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.utility.nbt.SaveManager;

/** @author Darkguardsman */
public class SentryRegistry
{
    private static HashMap<String, Class<? extends Sentry>> sentryMap = new HashMap<String, Class<? extends Sentry>>();

    public static void registrySentry(String name, Class<? extends Sentry> sentry)
    {
        synchronized (sentryMap)
        {
            if (!sentryMap.containsKey(name))
            {
                sentryMap.put(name, sentry);
                SaveManager.registerClass("Sentry-" + name, sentry);
            }
        }
    }

    public static Class<? extends Sentry> get(String name)
    {
        return sentryMap.get(name);
    }

    public static HashMap<String, Class<? extends Sentry>> getMap()
    {
        return sentryMap;
    }

    public static String getID(Sentry sentry)
    {
        for (Entry<String, Class<? extends Sentry>> entry : sentryMap.entrySet())
        {
            if (entry.getValue() != null && entry.getValue().equals(sentry))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Sentry build(NBTTagCompound compoundTag)
    {
        Object object = SaveManager.createAndLoad(compoundTag);
        if (object instanceof Sentry)
        {
            return (Sentry) object;
        }
        return null;
    }
}
