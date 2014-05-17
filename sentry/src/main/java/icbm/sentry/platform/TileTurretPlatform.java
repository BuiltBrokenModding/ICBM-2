package icbm.sentry.platform;

import icbm.sentry.interfaces.IWeaponProvider;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import resonant.lib.prefab.tile.TileElectricalInventory;
import resonant.lib.utility.MathUtility;
import universalelectricity.api.CompatibilityModule;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.vector.Vector3;

/** Turret Platform
 * 
 * @author DarkGuardsman */
public class TileTurretPlatform extends TileElectricalInventory
{
    private TileTurret[] turrets = new TileTurret[6];

    private static int[] ammoBaySlots = MathUtility.generateSqeuncedArray(0, 12);

    public TileTurretPlatform()
    {
        super();
        maxSlots = 20;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        /** Consume electrical items. */
        if (!this.worldObj.isRemote)
        {
            for (int i = 0; i < this.getSizeInventory(); i++)
            {
                if (this.getStackInSlot(i) != null && CompatibilityModule.isHandler(this.getStackInSlot(i).getItem()))
                {
                    long charge = CompatibilityModule.dischargeItem(this.getStackInSlot(i), Integer.MAX_VALUE, false);
                    CompatibilityModule.dischargeItem(this.getStackInSlot(i), this.onReceiveEnergy(ForgeDirection.UNKNOWN, charge, true), true);
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
    public boolean canConnect(ForgeDirection direction, Object source)
    {
        return true;
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
    public EnergyStorageHandler getEnergyHandler()
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
    public long onExtractEnergy(ForgeDirection from, long extract, boolean doExtract)
    {
        return 0;
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