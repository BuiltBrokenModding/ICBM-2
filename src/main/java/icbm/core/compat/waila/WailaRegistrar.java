package icbm.core.compat.waila;

import icbm.Settings;
import icbm.core.blocks.TileCamouflage;
import mcp.mobius.waila.api.IWailaRegistrar;

/**
 * @author tgame14
 * @since 12/04/14
 */
public class WailaRegistrar
{
	public static final String wailaCamoBlockHide = "wailaCamoBlockWailaHide";
	public static void wailaCallBack (IWailaRegistrar registrar)
	{
		//registrar.registerBodyProvider(new WailaTurretDataProvider(), TileTurret.class);
		registrar.registerStackProvider(new WailaCamoDataProvider(), TileCamouflage.class);
		registrar.addConfig(Settings.DOMAIN, wailaCamoBlockHide, "Hide Camo block waila tooltip?");
	}
}
