package icbm.sentry.turret;

import icbm.sentry.turret.sentryhandler.Sentry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.utility.nbt.SaveManager;

/** @author Darkguardsman, tgame14 */
public class SentryRegistry
{
    private static LinkedList<Class<? extends Sentry>> sentryList = new LinkedList<Class<? extends Sentry>>();

    public static void registerSentry (String key, Class<? extends Sentry> sentry)
    {
        synchronized (sentryList)
        {
            if (!sentryList.contains(sentry))
            {
                sentryList.add(sentry);
                SaveManager.registerClass(key, sentry);
            }

        }
    }

    public static List<Class<? extends Sentry>> getSentryList ()
    {
        return sentryList;
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
