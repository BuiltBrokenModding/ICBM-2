package icbm.core.tiles;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import resonant.lib.gui.ContainerBase;

/** @author Darkguardsman */
public class ContainerBox extends ContainerBase
{
    public ContainerBox(InventoryPlayer invPlayer, TileBox tile)
    {
        super(tile);

        int slotID = 0;

        for (int xSlot = 0; xSlot < 9; xSlot++)
        {
            for (int ySlot = 0; ySlot < 3; ySlot++)
            {
                this.addSlotToContainer(new Slot(tile, slotID, 8 + 18 * xSlot, 28 + 18 * ySlot));
                slotID++;
            }
        }

        this.addPlayerInventory(invPlayer.player);
        tile.openChest();
    }
}
