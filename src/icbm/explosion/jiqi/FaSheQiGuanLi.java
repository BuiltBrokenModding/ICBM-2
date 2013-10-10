package icbm.explosion.jiqi;

import java.util.HashSet;

import universalelectricity.core.vector.Vector2;

public class FaSheQiGuanLi
{
	private static HashSet<TFaSheQi> FA_SHE_QI = new HashSet<TFaSheQi>();

	public static void registerFaSheQi(TFaSheQi launcher)
	{
		if (!launcher.isInvalid())
		{
			FA_SHE_QI.add(launcher);
		}
		else
		{
			unregisterFaSheQi(launcher);
		}
	}

	public static void unregisterFaSheQi(TFaSheQi launcher)
	{
		FA_SHE_QI.remove(launcher);
	}

	public static HashSet<TFaSheQi> naFaSheQiInArea(Vector2 minVector, Vector2 maxVector)
	{
		HashSet<TFaSheQi> returnArray = new HashSet<TFaSheQi>();

		for (TFaSheQi launcher : FA_SHE_QI)
		{
			if (launcher.xCoord > minVector.x && launcher.xCoord < maxVector.x && launcher.zCoord > minVector.y && launcher.zCoord < maxVector.y)
			{
				returnArray.add(launcher);
			}
		}

		return returnArray;
	}

	public static HashSet<TFaSheQi> getFaSheQi()
	{
		return FA_SHE_QI;
	}
}
