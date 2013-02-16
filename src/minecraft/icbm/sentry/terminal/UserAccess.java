package icbm.sentry.terminal;

import net.minecraft.nbt.NBTTagCompound;

public class UserAccess
{
	public String username;
	public AccessLevel level;
	public boolean shouldSave;

	public UserAccess(String user, AccessLevel level, boolean save)
	{
		this.username = user;
		this.level = level;
		this.shouldSave = save;
	}

	/**
	 * Write to nbt
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("username", this.username);
		nbt.setInteger("ID", this.level.ordinal());
		return nbt;
	}

	/**
	 * Read from nbt
	 */
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.username = nbt.getString("username");
		this.level = AccessLevel.get(nbt.getInteger("ID"));
	}

	public static UserAccess loadFromNBT(NBTTagCompound nbt)
	{
		UserAccess access = new UserAccess("", AccessLevel.NONE, true);
		access.readFromNBT(nbt);
		return access;
	}
}
