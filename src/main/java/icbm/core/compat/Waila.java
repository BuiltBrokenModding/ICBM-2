package icbm.core.compat;

import icbm.core.compat.waila.WailaRegistrar;
import resonant.lib.loadable.ILoadable;
import resonant.lib.compat.Mods;
import cpw.mods.fml.common.event.FMLInterModComms;

/**
 * @author tgame14
 * @since 12/04/14
 */
public class Waila implements ILoadable
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
}
