package icbm.core.compat;

import calclavia.lib.modproxy.ICompatProxy;
import calclavia.lib.utility.Mods;
import cpw.mods.fml.common.event.FMLInterModComms;
import icbm.core.ICBMCore;

/**
 * @author tgame14
 * @since 12/04/14
 */
public class Waila implements ICompatProxy
{
	@Override
	public void preInit()
	{

	}

	@Override
	public void init()
	{
		ICBMCore.LOGGER.info("CALLING MOD WAILA LOADUP \n\n\n\n\n\n\n\n\n\n");
		FMLInterModComms.sendMessage(Mods.WAILA(), "register", "icbm.core.compat.waila.WailaRegistrar.wailaCallBack");
	}

	@Override
	public void postInit()
	{

	}

	@Override
	public String modId()
	{
		return Mods.WAILA();
	}
}
