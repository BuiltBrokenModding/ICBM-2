package icbm.gangshao;

import icbm.gangshao.terminal.AccessLevel;
import icbm.gangshao.terminal.UserAccess;

import java.util.List;

public interface ISpecialAccess
{
	/**
	 * Gets the player's access level on the machine he is using
	 * 
	 * @return access level of the player, make sure to return no access if the player doesn't have
	 * any
	 */
	public AccessLevel getUserAccess(String username);

	/**
	 * gets the access list for the machine
	 * 
	 * @return hasMap of players and there access levels
	 */
	public List<UserAccess> getUsers();

	/**
	 * sets the players access level in the access map
	 * 
	 * @param player
	 * @return true if the level was set false if something went wrong
	 */
	public boolean addUserAccess(String username, AccessLevel level, boolean save);

	/**
	 * Removes the user from the access list
	 * 
	 * @param username
	 * @return
	 */
	public boolean removeUserAccess(String username);

	/**
	 * Gets a list of users with this specified access level.
	 * 
	 * @param level
	 * @return
	 */
	List<UserAccess> getUsersWithAcess(AccessLevel level);

}
