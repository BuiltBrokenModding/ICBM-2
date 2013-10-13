package mffs.api.security;

public class Permission
{
	// TODO: FIX NAMES
	/**
	 * Force Field Wrap - Allows a player to go through force fields.
	 */
	public static final Permission FORCE_FIELD_WARP = new Permission(0, "warp");
	/**
	 * Place Access - Allows to open GUIs and activate blocks.
	 */
	public static final Permission BLOCK_ALTER = new Permission(1, "blockPlaceAccess");
	/**
	 * Block Access - Allows block access and opening GUIs.
	 */
	public static final Permission BLOCK_ACCESS = new Permission(2, "blockAccess");
	/**
	 * Configure - Allows to configure biometric identifiers.
	 */
	public static final Permission SECURITY_CENTER_CONFIGURE = new Permission(3, "configure");
	/**
	 * Bypass Confiscation - Allows the bypassing of interdiction matrix confiscation.
	 */
	public static final Permission BYPASS_INTERDICTION_MATRIX = new Permission(4, "bypassDefense");
	/**
	 * Remote Control - Allows the usage of a remote control to open GUIs remotely.
	 */
	public static final Permission DEFENSE_STATION_CONFISCATION = new Permission(5, "bypassConfiscation");
	/**
	 * Remote Control - Allows player to remotely control blocks with the remote.
	 */
	public static final Permission REMOTE_CONTROL = new Permission(6, "remoteControl");

	private static Permission[] LIST;

	public final int id;
	public final String name;

	public Permission(int id, String name)
	{
		this.id = id;
		this.name = name;

		if (LIST == null)
		{
			LIST = new Permission[7];
		}

		LIST[this.id] = this;
	}

	public static Permission getPermission(int id)
	{
		if (id < LIST.length && id >= 0)
		{
			return LIST[id];
		}

		return null;
	}

	public static Permission[] getPermissions()
	{
		return LIST;
	}
}