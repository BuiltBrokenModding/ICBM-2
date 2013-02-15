package universalelectricity.prefab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
@Deprecated
public class UpdateNotifier implements IPlayerTracker
{
	public static final UpdateNotifier INSTANCE = new UpdateNotifier();
	public static boolean isInitialized = false;

	public static final HashMap<String, String> MODS_TO_UPDATE = new HashMap<String, String>();

	/**
	 * Add variables to this array if you want extra custom notifications to be output.
	 */
	public static final List<String> NOTIFICATIONS = new ArrayList<String>();

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
			MODS_TO_UPDATE.put(modName, latestUpdate.trim());
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
		if (MODS_TO_UPDATE.size() > 0)
		{
			// Output Notification Message.
			String updateNotification = "Please update the following mod(s): ";

			Iterator it = MODS_TO_UPDATE.entrySet().iterator();

			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry) it.next();

				updateNotification += pairs.getKey() + " [" + pairs.getValue() + "] ";
			}

			player.addChatMessage(updateNotification);
		}

		// Output Extra Notifications
		for (String notification : NOTIFICATIONS)
		{
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
