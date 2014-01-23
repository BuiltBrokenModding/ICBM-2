package icbm.sentry.turret;

import java.util.HashMap;
import java.util.Map.Entry;

/** @author Darkguardsman */
public class SentryRegistry
{
    private static HashMap<String, Sentry> sentryMap = new HashMap<String, Sentry>();

    public static void registrySentry(String name, Sentry sentry)
    {
        synchronized (sentryMap)
        {
            if (!sentryMap.containsKey(name))
            {
                sentryMap.put(name, sentry);
            }
        }
    }

    public static Sentry get(String name)
    {
        return sentryMap.get(name);
    }

    public static HashMap<String, Sentry> getMap()
    {
        return sentryMap;
    }

    public static String getID(Sentry sentry)
    {
        for (Entry<String, Sentry> entry : sentryMap.entrySet())
        {
            if (entry.getValue() != null && entry.getValue().equals(sentry))
            {
                return entry.getKey();
            }
        }
        return null;
    }
}
