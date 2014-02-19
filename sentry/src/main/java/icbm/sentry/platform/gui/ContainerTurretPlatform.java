package icbm.sentry.platform.gui;

import icbm.sentry.platform.TileTurretPlatform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import calclavia.lib.gui.ContainerBase;
import calclavia.lib.prefab.slot.SlotEnergyItem;
import calclavia.lib.prefab.slot.SlotSpecific;

/**
 * Nuclear boiler container
 */
public class ContainerTurretPlatform extends ContainerBase
{
	private static final int slotCount = 4;
	private TileTurretPlatform tileEntity;

	public ContainerTurretPlatform(InventoryPlayer par1InventoryPlayer, TileTurretPlatform tileEntity)
	{
		super(tileEntity);
		this.tileEntity = tileEntity;

		int i = 0;

		for (int xSlot = 0; xSlot < 4; xSlot++)
		{
			for (int ySlot = 0; ySlot < 5; ySlot++)
			{
				this.addSlotToContainer(new Slot(tileEntity, i, 95 + 18 * xSlot, 18 + 18 * ySlot));
				i++;
			}
		}

		this.addPlayerInventory(par1InventoryPlayer.player);
		tileEntity.openChest();
	}
}
