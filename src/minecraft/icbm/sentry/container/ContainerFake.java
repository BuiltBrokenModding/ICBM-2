package icbm.sentry.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

public class ContainerFake extends Container
{
	TileEntity entity = null;

	public ContainerFake(TileEntity tileEntityLC)
	{
		this.entity = tileEntityLC;
	}

	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		if (entity instanceof IInventory)
		{
			return ((IInventory) this.entity).isUseableByPlayer(par1EntityPlayer);
		}
		return true;// TODO might have to toy with this
	}

}
