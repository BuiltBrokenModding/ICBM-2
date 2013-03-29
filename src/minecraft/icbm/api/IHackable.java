package icbm.api;

import icbm.gangshao.ISpecialAccess;
import net.minecraft.entity.player.EntityPlayer;

public interface IHackable extends ISpecialAccess
{
	/**
	 * Causes the machine to generate a new pass key
	 */
	public void generateNewKey();

	/**
	 * Checks to see if the pass key matches the stored one
	 */
	public boolean tryForAccess(EntityPlayer player, String pass);
}
