package icbm.compat.waila;

import icbm.content.tile.TileCamouflage;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author tgame14
 * @since 24/04/14
 */
public class WailaCamoDataProvider implements IWailaDataProvider
{
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileCamouflage tile = (TileCamouflage) accessor.getTileEntity();
		return tile.block != Blocks.air && config.getConfig(WailaRegistrar.wailaCamoBlockHide) ? new ItemStack(tile.block, 0, tile.blockMeta) : null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}
}
