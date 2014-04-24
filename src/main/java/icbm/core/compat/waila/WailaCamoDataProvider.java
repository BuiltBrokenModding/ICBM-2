package icbm.core.compat.waila;

import icbm.core.blocks.TileCamouflage;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.List;

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
		return tile.getMimicBlockID() != 0 && config.getConfig(WailaRegistrar.wailaCamoBlockHide) ? new ItemStack(Block.blocksList[tile.getMimicBlockID()], 0, tile.getMimicBlockMeta()) : null;
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
