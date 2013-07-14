package icbm.gangshao.access;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class UserAccess {
	public String username;
	public AccessLevel level;
	public boolean shouldSave;

	public UserAccess(String user, AccessLevel level, boolean save) {
		this.username = user;
		this.level = level;
		this.shouldSave = save;
	}

	/**
	 * Write to nbt
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("username", this.username);
		nbt.setInteger("ID", this.level.ordinal());
		return nbt;
	}

	/**
	 * Read from nbt
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		this.username = nbt.getString("username");
		this.level = AccessLevel.get(nbt.getInteger("ID"));
	}

	public static UserAccess loadFromNBT(NBTTagCompound nbt) {
		UserAccess access = new UserAccess("", AccessLevel.NONE, true);
		access.readFromNBT(nbt);
		return access;
	}

	/**
	 * Reads an entire UserAccess list from an nbt file
	 * 
	 * @param nbt
	 *            - nbt being read
	 * @return - the list
	 */
	public static List<UserAccess> readListFromNBT(NBTTagCompound nbt,
			String tagName) {

		NBTTagList userList = nbt.getTagList(tagName);
		List<UserAccess> users = new ArrayList<UserAccess>();
		for (int i = 0; i < userList.tagCount(); ++i) {
			NBTTagCompound var4 = (NBTTagCompound) userList.tagAt(i);
			users.add(UserAccess.loadFromNBT(var4));
		}
		return users;
	}

	/**
	 * writes an entire UserAccess list to nbt at one time
	 * 
	 * @param save
	 *            - nbt to save to
	 * @param users
	 *            - list to save
	 */
	public static void writeListToNBT(NBTTagCompound save,
			List<UserAccess> users) {
		NBTTagList usersTag = new NBTTagList();
		for (int player = 0; player < users.size(); ++player) {
			UserAccess access = users.get(player);
			if (access != null && access.shouldSave) {
				NBTTagCompound accessData = new NBTTagCompound();
				access.writeToNBT(accessData);
				usersTag.appendTag(accessData);
			}
		}

		save.setTag("Users", usersTag);
	}

	/**
	 * Removes a user from a list of UserAccess then returns that list
	 */
	public static List<UserAccess> removeUserAccess(String player,
			List<UserAccess> users) {
		List<UserAccess> removeList = new ArrayList<UserAccess>();
		List<UserAccess> returnList = users;
		for (int i = 0; i < users.size(); i++) {
			UserAccess ref = users.get(i);
			if (ref.username.equalsIgnoreCase(player)) {
				removeList.add(ref);
			}
		}
		if (removeList != null && removeList.size() > 0) {
			returnList.removeAll(removeList);
		}
		return returnList;
	}
}
