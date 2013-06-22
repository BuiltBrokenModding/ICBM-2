package icbm.gangshao.access;

import icbm.gangshao.saving.INbtSave;
import icbm.gangshao.saving.SaveManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class GlobalAccessLoader implements INbtSave
{
	public static boolean isInitialized = false;

	public static GlobalAccessLoader intance = new GlobalAccessLoader();

	/** Name of the save file **/
	public static final String SAVE_NAME = "Global_Access_List";

	public void initiate()
	{
		if (!isInitialized)
		{
			MinecraftForge.EVENT_BUS.register(this);
			SaveManager.intance.registerNbtSave(this);
			isInitialized = true;
		}
	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		if (!GlobalAccessManager.hasLoaded)
		{
			GlobalAccessManager.getMasterSaveFile();
		}
	}

	@Override
	public String saveFileName()
	{
		return GlobalAccessLoader.SAVE_NAME;
	}

	@Override
	public NBTTagCompound getSaveData()
	{
		return GlobalAccessManager.getMasterSaveFile();
	}

	@Override
	public boolean shouldSave(boolean isServer)
	{
		return isServer && GlobalAccessManager.hasLoaded && !GlobalAccessManager.loading;
	}
}
