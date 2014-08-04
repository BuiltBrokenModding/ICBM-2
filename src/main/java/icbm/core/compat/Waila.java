package icbm.core.compat;

import icbm.core.compat.waila.WailaRegistrar;
import resonant.lib.modproxy.ICompatProxy;
import resonant.lib.utility.Mods;
import cpw.mods.fml.common.event.FMLInterModComms;

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
		FMLInterModComms.sendMessage(Mods.WAILA(), "register", WailaRegistrar.class.getName() + ".wailaCallBack");
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
