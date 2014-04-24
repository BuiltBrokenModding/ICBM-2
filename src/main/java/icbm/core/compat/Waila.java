package icbm.core.compat;

import calclavia.lib.modproxy.ICompatProxy;
import calclavia.lib.utility.Mods;
import cpw.mods.fml.common.event.FMLInterModComms;
import icbm.core.compat.waila.WailaRegistrar;

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
