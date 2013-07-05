package icbm.gangshao;

import icbm.gangshao.access.AccessLevel;
import icbm.gangshao.terminal.ITerminal;
import icbm.gangshao.turret.ItemAmmo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import universalelectricity.core.item.IItemElectric;
import universalelectricity.prefab.SlotSpecific;

public class SlotAmmunition extends SlotSpecific
{
	public SlotAmmunition(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4, IAmmunition.class, IItemElectric.class);
	}

	@Override
	public boolean canTakeStack(EntityPlayer entityPlayer)
	{
		if (this.inventory instanceof ITerminal)
		{
			return ((ITerminal) this.inventory).getUserAccess(entityPlayer.username).ordinal() > AccessLevel.NONE.ordinal();
		}

		return false;
	}
}
