package icbm.zhapin.zhapin;

public enum ZhaPinType
{
	QUAN_BU, ZHA_DAN, SHOU_LIU_DAN, DAO_DAN, CHE;

	public static ZhaPinType get(int id)
	{
		if (id >= 0 && id < ZhaPinType.values().length)
		{
			return ZhaPinType.values()[id];
		}

		return null;
	}
}
