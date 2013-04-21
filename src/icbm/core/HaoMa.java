package icbm.core;

import java.util.HashMap;

public class HaoMa
{
	private static final HashMap<String, Integer> IDS = new HashMap<String, Integer>();

	public static int getID(String name, int defaultID)
	{
		int id = defaultID;

		if (IDS.containsKey(name))
		{
			id = IDS.get(name);
			id++;
		}

		IDS.put(name, id);

		return id;
	}

	public static int getID(String name)
	{
		return getID(name, 0);
	}
}
