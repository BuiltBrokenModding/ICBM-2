package icbm.explosion.explosive;

import icbm.explosion.ex.Explosion;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/** Registry for all missiles
 * 
 * @author Calcalvia */
public class ExplosiveRegistry
{
    private static int maxID = 0;
    private static final HashMap<Integer, Explosive> idToExplosiveMap = new HashMap<Integer, Explosive>();
    private static final BiMap<Integer, String> idToNameMap = HashBiMap.create();

    public static Explosive register(Explosive zhaPin)
    {
        if (!isRegistered(zhaPin))
        {
            int nextID = maxID++;
            idToExplosiveMap.put(nextID, zhaPin);
            idToNameMap.put(nextID, zhaPin.getUnlocalizedName());
            return zhaPin;
        }
        return null;
    }

    public static boolean isRegistered(Explosive explosive)
    {
        return idToNameMap.containsKey(explosive.getUnlocalizedName());
    }

    public static int getID(String unlocalizedName)
    {
        return idToNameMap.inverse().get(unlocalizedName);
    }

    public static Explosive get(String name)
    {
        return idToExplosiveMap.get(getID(name));
    }

    public static Explosive get(int haoMa)
    {
        return idToExplosiveMap.get(haoMa);
    }

    public static String getName(int haoMa)
    {
        return idToNameMap.get(haoMa);
    }

    public static Collection<Explosive> getExplosives()
    {
        return idToExplosiveMap.values();
    }

    public static Collection<Explosion> getAllMissles()
    {
        Collection<Explosion> missiles = new HashSet<Explosion>();

        for (Explosive zhaPin : idToExplosiveMap.values())
        {
            if (zhaPin instanceof Explosion)
            {
                missiles.add((Explosion) zhaPin);
            }
        }

        return missiles;
    }

    public static HashMap<Integer, Explosive> getAll()
    {
        return idToExplosiveMap;
    }

}
