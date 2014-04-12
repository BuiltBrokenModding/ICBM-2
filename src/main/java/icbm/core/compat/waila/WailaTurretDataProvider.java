package icbm.core.compat.waila;

import calclavia.lib.access.IProfileContainer;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.TurretRegistry;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * @author tgame14
 * @since 12/04/14
 */
public class WailaTurretDataProvider implements IWailaDataProvider
{
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		Turret turret = TurretRegistry.constructSentry(accessor.getNBTData().getCompoundTag(ITurret.SENTRY_OBJECT_SAVE).getString(ITurret.SENTRY_TYPE_SAVE_ID), this);
		return TurretRegistry.getItemStack(turret.getClass());
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		// TODO: Add data here
		IProfileContainer container = (IProfileContainer) accessor.getTileEntity();
		if (!container.getAccessProfile().getOwnerGroup().isMemeber(accessor.getPlayer().username))
		{
			return currenttip;
		}
		currenttip.add(container.getAccessProfile().toString());

		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}
}
