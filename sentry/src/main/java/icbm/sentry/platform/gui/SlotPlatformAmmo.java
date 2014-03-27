package icbm.sentry.platform.gui;

import icbm.sentry.interfaces.IWeaponProvider;
import icbm.sentry.platform.TileTurretPlatform;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

/** Slot that only accept upgrades for sentries
 * 
 * @author DarkGuardsman */
public class SlotPlatformAmmo extends Slot
{
    private TileTurretPlatform platform;

    public SlotPlatformAmmo(TileTurretPlatform tile, int par2, int par3, int par4)
    {
        super(tile, par2, par3, par4);
        this.platform = tile;
    }

    @Override
    public boolean isItemValid(ItemStack compareStack)
    {
        if (platform != null)
        {
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            {
                TileTurret tileTurret = platform.getTurret(direction);
                if (tileTurret.getTurret() instanceof IWeaponProvider)
                {
                    if (((IWeaponProvider) tileTurret.getTurret()).getWeaponSystem() != null)
                    {
                        return ((IWeaponProvider) tileTurret.getTurret()).getWeaponSystem().isAmmo(compareStack);
                    }
                }
            }
        }
        return compareStack != null;
    }
}
