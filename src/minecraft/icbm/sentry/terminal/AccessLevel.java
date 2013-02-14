package icbm.sentry.terminal;

public enum AccessLevel
{
	NONE("None"), MINOR("Some"), BASIC("trivial"), STANARD("Limited"), USER("User"), OPERATOR("Admin"), OWNER("Root-Admin");

	public String displayName;

	private AccessLevel(String name)
	{
		displayName = name;
	}

}
