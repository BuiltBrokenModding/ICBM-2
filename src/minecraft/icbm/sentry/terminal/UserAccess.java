package icbm.sentry.terminal;

public class UserAccess
{
	public String username;
	public AccessLevel access;
	public Boolean shouldSave;

	public UserAccess(String user, AccessLevel lvl, Boolean save)
	{
		username = user;
		access = lvl;
		shouldSave = save;
	}
}
