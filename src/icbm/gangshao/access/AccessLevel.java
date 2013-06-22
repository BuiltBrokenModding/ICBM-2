package icbm.gangshao.access;

public enum AccessLevel
{
	NONE("None"), BASIC("Basic"), USER("Standard"), ADMIN("Admin"), OWNER("Owner");

	public String displayName;

	private AccessLevel(String name)
	{
		displayName = name;
	}

	/**
	 * Gets the access level in varies ways
	 * 
	 * @return AccessLevel NONE instead of null if correct level can't be found.
	 */
	public static AccessLevel get(Object ob)
	{
		if (ob instanceof String)
		{
			for (AccessLevel access : AccessLevel.values())
			{
				if (access.displayName.equalsIgnoreCase((String) ob) || access.name().equalsIgnoreCase((String) ob))
				{
					return access;
				}
			}
		}
		if (ob instanceof Integer)
		{
			int i = (Integer) ob;

			if (i >= 0 && i < AccessLevel.values().length)
			{
				return AccessLevel.values()[i];
			}
		}

		return NONE;
	}
}
