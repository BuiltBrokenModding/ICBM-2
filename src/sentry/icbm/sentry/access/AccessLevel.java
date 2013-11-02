package icbm.sentry.access;

public enum AccessLevel
{
	/** No access granted. */
	NONE("None"),
	/** >= Owners can do anything */
	USER("User"),
	/** >= Admins can set rank */
	ADMIN("Admin"),

	/** Standards won't get shot */

	OWNER("Owner");

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
			int i = ((Integer) ob) % AccessLevel.values().length;

			if (i >= 0 && i < AccessLevel.values().length)
			{
				return AccessLevel.values()[i];
			}
		}

		return NONE;
	}
}
