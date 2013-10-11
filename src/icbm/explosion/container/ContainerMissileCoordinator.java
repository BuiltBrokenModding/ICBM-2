package icbm.explosion.container;

import icbm.explosion.machines.TileEntityMissileCoordinator;
import mffs.api.card.ICoordLink;
import net.minecraft.entity.player.InventoryPlayer;
import universalelectricity.prefab.SlotSpecific;
import calclavia.lib.gui.ContainerBase;

public class ContainerMissileCoordinator extends ContainerBase
{
	public ContainerMissileCoordinator(InventoryPlayer inventoryPlayer, TileEntityMissileCoordinator tileEntity)
	{
		super(tileEntity);
		this.addSlotToContainer(new SlotSpecific(tileEntity, 0, 16, 41, ICoordLink.class));
		this.addSlotToContainer(new SlotSpecific(tileEntity, 1, 136, 41, ICoordLink.class));
		this.addPlayerInventory(inventoryPlayer.player);
	}
}
