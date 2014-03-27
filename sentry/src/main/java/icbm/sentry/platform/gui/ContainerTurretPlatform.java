package icbm.sentry.platform.gui;

import icbm.sentry.platform.TileTurretPlatform;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.gui.ContainerBase;

/** Container for Sentry platform */
public class ContainerTurretPlatform extends ContainerBase
{
    private TileTurretPlatform tileEntity;

    public ContainerTurretPlatform(InventoryPlayer par1InventoryPlayer, TileTurretPlatform tileEntity)
    {
        super(tileEntity);
        this.tileEntity = tileEntity;

        int i = 0;

        for (int xSlot = 0; xSlot < 4; xSlot++)
        {
            for (int ySlot = 0; ySlot < 3; ySlot++)
            {
                this.addSlotToContainer(new SlotPlatformAmmo(tileEntity, tileEntity.getTurret(ForgeDirection.UP), i, 95 + 18 * xSlot, 18 + 18 * ySlot));
                i++;
            }
        }

        for (int xSlot = 0; xSlot < 4; xSlot++)
        {
            this.addSlotToContainer(new SlotTurretUpgrade(tileEntity, i, 95 + 18 * xSlot, 90));
            i++;
        }

        this.addPlayerInventory(par1InventoryPlayer.player);
        tileEntity.openChest();
    }
}
