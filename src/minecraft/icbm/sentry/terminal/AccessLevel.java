package icbm.sentry.terminal;

public enum AccessLevel
{
	NONE("None"), BASIC("Basic"), USER("Standard"), OPERATOR("Admin"), OWNER("Root-Admin");

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
		if (ob instanceof Integer && ((Integer) ob) < AccessLevel.values().length)
		{
			return AccessLevel.values()[((Integer) ob)];
		}
		if (ob instanceof String)
		{
			for (AccessLevel a : AccessLevel.values())
			{
				if (a.name().equalsIgnoreCase(((String) ob)))
				{
					return a;
				}
			}
		}
		return NONE;
	}
}
