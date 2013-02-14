package icbm.sentry.terminal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public interface ISpecialAccess
{
	/**
	 * Gets the player's access level on the machine he is using
	 * 
	 * @return access level of the player, make sure to return no access if the player doesn't have
	 * any
	 */
	public AccessLevel getPlayerAccess(EntityPlayer player);

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
	public boolean setAccess(String player, AccessLevel lvl, boolean save);

}
