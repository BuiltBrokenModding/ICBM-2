package icbm.gangshao.saving;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Classes the register to need saving on world save use this
 * 
 * @author DarkGuardsman
 * 
 */
public interface INbtSave
{
	/**
	 * gets the file name to save as
	 */
	public String saveFileName();

	/**
	 * the data to save when saving to the file
	 */
	public NBTTagCompound getSaveData();

	/**
	 * can the file be saved at this moment
	 */
	public boolean shouldSave(boolean isServer);

}
