package icbm.api.sentry;

import java.util.HashMap;
import java.util.Map.Entry;

/** @author Darkguardsman */
public class SentryRegistry
{
    private static HashMap<String, ISentry> sentryMap = new HashMap<String, ISentry>();

    public static void registrySentry(String name, ISentry sentry)
    {
        synchronized (sentryMap)
        {
            if (!sentryMap.containsKey(name))
            {
                sentryMap.put(name, sentry);
            }
        }
    }

    public static ISentry get(String name)
    {
        return sentryMap.get(name);
    }

    public static HashMap<String, ISentry> getMap()
    {
        return sentryMap;
    }

    public static String getID(ISentry sentry)
    {
        for (Entry<String, ISentry> entry : sentryMap.entrySet())
        {
            if (entry.getValue() != null && entry.getValue().equals(sentry))
            {
                return entry.getKey();
            }
        }
        return null;
    }
}
