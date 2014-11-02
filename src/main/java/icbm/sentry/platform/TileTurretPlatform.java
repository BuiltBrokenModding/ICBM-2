package icbm.sentry.platform;

import icbm.sentry.interfaces.IWeaponProvider;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.electric.EnergyStorage;
import resonant.api.electric.IEnergyContainer;
import resonant.lib.grid.Compatibility;
import resonant.lib.utility.MathUtility;
import resonant.lib.content.prefab.java.TileElectricInventory;
/** Turret Platform
 * 
 * @author DarkGuardsman */
public class TileTurretPlatform extends TileElectricInventory
{
    private TileTurret[] turrets = new TileTurret[6];

    private static int[] ammoBaySlots = MathUtility.generateSqeuncedArray(0, 12);

    public TileTurretPlatform()
    {
        super();
        this.setSizeInventory(20);
    }

    @Override
    public void update()
    {
        super.update();

        /** Consume electrical items. */
        if (!this.worldObj.isRemote)
        {
            for (int i = 0; i < this.getSizeInventory(); i++)
            {
                if (this.getStackInSlot(i) != null && Compatibility.isHandler(this.getStackInSlot(i).getItem(), ForgeDirection.UNKNOWN))
                {
                    double charge = Compatibility.dischargeItem(this.getStackInSlot(i), Integer.MAX_VALUE, false);
                    Compatibility.dischargeItem(this.getStackInSlot(i), this.onReceiveEnergy(ForgeDirection.UNKNOWN, charge, true), true);
                }
            }
        }

        // TODO: Changed this to only update on block events
        turrets = new TileTurret[6];

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            TileEntity checkTile = new Vector3(this).translate(dir).getTileEntity(worldObj);

            if (checkTile instanceof TileTurret)
                turrets[dir.ordinal()] = (TileTurret) checkTile;
        }
    }

    public TileTurret getTurret(ForgeDirection side)
    {
        return turrets[side.ordinal()];
    }

    @Override
    public long onReceiveEnergy(ForgeDirection from, long receive, boolean doReceive)
    {
        long used = 0;
        long remain = receive;

        for (int i = 0; i < 6; i++)
        {
            TileTurret turret = this.turrets[i];

            if (turret != null && turret.getTurret() instanceof IEnergyContainer)
            {
                long added = turret.getTurret().battery.receiveEnergy(remain, doReceive);
                used += added;
                remain -= added;
            }
        }

        return used;
    }

    @Override
    public EnergyStorage getEnergyHandler()
    {
        for (TileTurret turretTile : turrets)
        {
            if (turretTile != null && turretTile.getTurret() != null && turretTile.getTurret() instanceof IEnergyContainer)
            {
                return turretTile.getTurret().battery;
            }
        }
        return super.getEnergyHandler();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return ammoBaySlots;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (stack != null)
        {
            if (CompatibilityModule.isHandler(stack.getItem()))
            {
                return true;
            }
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            {
                TileTurret tileTurret = getTurret(direction);
                if (tileTurret != null && tileTurret.getTurret() instanceof IWeaponProvider)
                {
                    if (((IWeaponProvider) tileTurret.getTurret()).getWeaponSystem() != null)
                    {
                        return ((IWeaponProvider) tileTurret.getTurret()).getWeaponSystem().isAmmo(stack);
                    }
                }
            }
        }
        return false;
    }
}