package icbm.sentry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import dark.lib.interfaces.ITerminal;

public class SlotTurret extends SlotSpecific
{
    public SlotTurret(IInventory par2iInventory, int par3, int par4, int par5, Class... validClasses)
    {
        super(par2iInventory, par3, par4, par5, validClasses);
    }

    @Override
    public boolean canTakeStack(EntityPlayer entityPlayer)
    {
        if (this.inventory instanceof ITerminal)
        {
            return ((ITerminal) this.inventory).getUserAccess(entityPlayer.username) != null;
        }

        return false;
    }
}
