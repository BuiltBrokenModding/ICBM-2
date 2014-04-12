package icbm.core.compat.waila;

import icbm.sentry.turret.block.TileTurret;
import mcp.mobius.waila.api.IWailaRegistrar;

/**
 * @author tgame14
 * @since 12/04/14
 */
public class WailaRegistrar
{
	public static void wailaCallBack (IWailaRegistrar registrar)
	{
		registrar.registerBodyProvider(new WailaTurretDataProvider(), TileTurret.class);
	}
}
