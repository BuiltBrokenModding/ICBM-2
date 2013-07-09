package icbm.zhapin.baozha.ex;

import icbm.api.explosion.IExplosive;
import icbm.zhapin.baozha.BaoZha;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ZhaPinRegistry
{
	private static int maxZhaPinID = 0;
	private static HashMap<String, ZhaPin> zhaPinMap = new HashMap();
	private static BiMap<String, Integer> zhaPinIDs = HashBiMap.create();

	private static int maxBaoZhaID = 0;
	private static HashMap<Integer, BaoZha> baoZhaMap = new HashMap();

	public static ZhaPin register(ZhaPin zhaPin)
	{
		if (!isRegistered(zhaPin))
		{
			zhaPinMap.put(zhaPin.getUnlocalizedName(), zhaPin);
			zhaPinIDs.put(zhaPin.getUnlocalizedName(), ++maxZhaPinID);
			return zhaPin;
		}

		return null;
	}

	public static BaoZha register(BaoZha baoZha)
	{
		if (!baoZhaMap.containsValue(baoZha))
		{
			baoZhaMap.put(++maxBaoZhaID, baoZha);
			return baoZha;
		}

		return null;
	}

	public static boolean isRegistered(ZhaPin zhaPin)
	{
		return zhaPinIDs.containsKey(zhaPin.getUnlocalizedName());
	}

	public static int getZhaPinID(String unlocalizedName)
	{
		return zhaPinIDs.get(unlocalizedName);
	}

	public static ZhaPin getZhaPin(String name)
	{
		return zhaPinMap.get(name);
	}

	public static Collection<ZhaPin> getRegisteredZhaPin()
	{
		return zhaPinMap.values();
	}

	public static IExplosive getZhaPin(int haoMa)
	{
		return zhaPinMap.get(getZhaPinName(haoMa));
	}

	public static String getZhaPinName(int haoMa)
	{
		return zhaPinIDs.inverse().get(haoMa);
	}

}
