package icbm.explosion.missile;

import icbm.explosion.missile.missile.Missile;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ExplosiveRegistry
{
	private static int maxID = 0;
	private static HashMap<Integer, Explosive> zhaPinMap = new HashMap();
	private static BiMap<Integer, String> zhaPinIDs = HashBiMap.create();

	public static Explosive register(Explosive zhaPin)
	{
		if (!isRegistered(zhaPin))
		{
			int nextID = maxID++;
			zhaPinMap.put(nextID, zhaPin);
			zhaPinIDs.put(nextID, zhaPin.getUnlocalizedName());
			return zhaPin;
		}

		return null;
	}

	public static boolean isRegistered(Explosive zhaPin)
	{
		return zhaPinIDs.containsKey(zhaPin.getUnlocalizedName());
	}

	public static int getID(String unlocalizedName)
	{
		return zhaPinIDs.inverse().get(unlocalizedName);
	}

	public static Explosive get(String name)
	{
		return zhaPinMap.get(getID(name));
	}

	public static Explosive get(int haoMa)
	{
		return zhaPinMap.get(haoMa);
	}

	public static String getName(int haoMa)
	{
		return zhaPinIDs.get(haoMa);
	}

	public static Collection<Explosive> getAllZhaPin()
	{
		return zhaPinMap.values();
	}

	public static Collection<Missile> getAllDaoDan()
	{
		Collection<Missile> daoDans = new HashSet<Missile>();

		for (Explosive zhaPin : zhaPinMap.values())
		{
			if (zhaPin instanceof Missile)
			{
				daoDans.add((Missile) zhaPin);
			}
		}

		return daoDans;
	}

	public static HashMap<Integer, Explosive> getAll()
	{
		return zhaPinMap;
	}

}
