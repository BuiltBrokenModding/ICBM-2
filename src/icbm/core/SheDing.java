package icbm.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;

/**
 * Settings class for various configuration settings.
 * 
 * @author Calclavia
 * 
 */
public class SheDing
{
	/**
	 * Configuration file for ICBM.
	 */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "ICBM.cfg"));

	/** Should ICBM use external fuel? **/
	public static boolean USE_FUEL = true;
	public static boolean ZAI_KUAI = true;
	public static int DAO_DAN_ZUI_YUAN = 10000;

	public static void initiate()
	{
		CONFIGURATION.load();
		USE_FUEL = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Use Fuel", SheDing.USE_FUEL).getBoolean(SheDing.USE_FUEL);
		ZAI_KUAI = SheDing.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Allow Chunk Loading", ZAI_KUAI).getBoolean(ZAI_KUAI);
		DAO_DAN_ZUI_YUAN = SheDing.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Max Missile Distance", SheDing.DAO_DAN_ZUI_YUAN).getInt(SheDing.DAO_DAN_ZUI_YUAN);
		CONFIGURATION.save();
	}

}
