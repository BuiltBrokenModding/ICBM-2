package icbm.core;

import icbm.api.IItemFrequency;
import icbm.contraption.block.TileCamouflage;
import icbm.contraption.block.TileProximityDetector;
import icbm.contraption.gui.GuiTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import calclavia.lib.gui.ContainerDummy;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler
{
	public void preInit()
	{
	}

	public void init()
	{
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileProximityDetector)
		{
			return new ContainerDummy(player, tileEntity);
		}
		else if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof IItemFrequency)
		{
			return new ContainerDummy(player, tileEntity);
		}

		return null;
	}
}
