package icbm.explosion.container;

import icbm.core.prefab.ContainerBase;
import icbm.explosion.machines.TileEntityMissileCoordinator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerMissileCoordinator extends ContainerBase
{
    public ContainerMissileCoordinator(InventoryPlayer inventoryPlayer, TileEntityMissileCoordinator tileEntity)
    {
        super(tileEntity);
        this.addSlotToContainer(new Slot(tileEntity, 0, 16, 41));
        this.addSlotToContainer(new Slot(tileEntity, 1, 136, 41));
        this.addPlayerInventory(inventoryPlayer.player);
    }
}
