package icbm.zhapin.zhapin;

import icbm.zhapin.zhapin.daodan.DaoDan;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ZhaPinRegistry {
	private static int maxID = 0;
	private static HashMap<Integer, ZhaPin> zhaPinMap = new HashMap();
	private static BiMap<Integer, String> zhaPinIDs = HashBiMap.create();

	public static ZhaPin register(ZhaPin zhaPin) {
		if (!isRegistered(zhaPin)) {
			int nextID = maxID++;
			zhaPinMap.put(nextID, zhaPin);
			zhaPinIDs.put(nextID, zhaPin.getUnlocalizedName());
			return zhaPin;
		}

		return null;
	}

	public static boolean isRegistered(ZhaPin zhaPin) {
		return zhaPinIDs.containsKey(zhaPin.getUnlocalizedName());
	}

	public static int getID(String unlocalizedName) {
		return zhaPinIDs.inverse().get(unlocalizedName);
	}

	public static ZhaPin get(String name) {
		return zhaPinMap.get(getID(name));
	}

	public static ZhaPin get(int haoMa) {
		return zhaPinMap.get(haoMa);
	}

	public static String getName(int haoMa) {
		return zhaPinIDs.get(haoMa);
	}

	public static Collection<ZhaPin> getAllZhaPin() {
		return zhaPinMap.values();
	}

	public static Collection<DaoDan> getAllDaoDan() {
		Collection<DaoDan> daoDans = new HashSet<DaoDan>();

		for (ZhaPin zhaPin : zhaPinMap.values()) {
			if (zhaPin instanceof DaoDan) {
				daoDans.add((DaoDan) zhaPin);
			}
		}

		return daoDans;
	}

	public static HashMap<Integer, ZhaPin> getAll() {
		return zhaPinMap;
	}

}
