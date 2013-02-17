package icbm.sentry;

import icbm.sentry.api.ITerminal;
import icbm.sentry.terminal.AccessLevel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import universalelectricity.prefab.SlotSpecific;

public class SlotAmmunition extends SlotSpecific
{
	public SlotAmmunition(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4, ICBMSentry.conventionalBullet.copy());
	}

	public boolean canTakeStack(EntityPlayer entityPlayer)
	{
		if (this.inventory instanceof ITerminal)
		{
			return ((ITerminal) this.inventory).getUserAccess(entityPlayer.username).ordinal() > AccessLevel.NONE.ordinal();
		}

		return false;
	}
}
