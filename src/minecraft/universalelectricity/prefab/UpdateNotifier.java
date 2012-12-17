package universalelectricity.prefab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * A class used to search online and check for updates for your mod and notify the player.
 * 
 * @author Calclavia
 * 
 */
public class UpdateNotifier implements IPlayerTracker
{
	public static final UpdateNotifier INSTANCE = new UpdateNotifier();
	public static boolean isInitialized = false;

	public static final HashMap<String, String> modsToUpdate = new HashMap<String, String>();

	/**
	 * Call this in your FML Pre-Initialize Event.
	 * 
	 * @param modName The name of your mod.
	 * @param currentVersion The current version of your mod.
	 * @param updateUrl The web address of your text file containing the latest version number.
	 * @return The latest version number, empty if fail to check for update.
	 */
	public final String checkUpdate(String modName, String currentVersion, String updateUrl)
	{
		if (!isInitialized)
		{
			GameRegistry.registerPlayerTracker(this);
			isInitialized = true;
		}

		String latestUpdate = getLatestVersion(updateUrl);

		if (latestUpdate != null && latestUpdate != "" && !currentVersion.trim().equals(latestUpdate.trim()))
		{
			modsToUpdate.put(modName, latestUpdate);
		}

		return latestUpdate;
	}

	/**
	 * Allows you to check for updates for your mod.
	 * 
	 * @param updateUrl The web address of your text file containing the latest version number.
	 * @return The latest version number, empty if fail to check for update.
	 */
	public static final String getLatestVersion(String updateUrl)
	{
		try
		{
			URL versionFile = new URL(updateUrl);
			BufferedReader reader = new BufferedReader(new InputStreamReader(versionFile.openStream()));
			return reader.readLine();
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to check for mod updates.");
		}

		return "";
	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (modsToUpdate.size() > 0)
		{
			String notification = "You have " + modsToUpdate.size() + " mod(s) that needs to be updated: ";

			Iterator it = modsToUpdate.entrySet().iterator();

			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry) it.next();

				notification += pairs.getKey() + " [" + pairs.getValue() + "] ";
			}

			player.addChatMessage(notification);
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{

	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{

	}
}
