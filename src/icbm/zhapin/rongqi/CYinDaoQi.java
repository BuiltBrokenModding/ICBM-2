package icbm.zhapin.rongqi;

import icbm.zhapin.jiqi.TYinDaoQi;
import mffs.api.card.ICoordLink;
import net.minecraft.entity.player.InventoryPlayer;
import universalelectricity.prefab.SlotSpecific;
import calclavia.lib.gui.ContainerBase;

public class CYinDaoQi extends ContainerBase
{
	public CYinDaoQi(InventoryPlayer inventoryPlayer, TYinDaoQi tileEntity)
	{
		super(tileEntity);
		this.addSlotToContainer(new SlotSpecific(tileEntity, 0, 16, 41, ICoordLink.class));
		this.addSlotToContainer(new SlotSpecific(tileEntity, 1, 136, 41, ICoordLink.class));
		this.addPlayerInventory(inventoryPlayer.player);
	}
}
