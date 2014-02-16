package icbm.sentry.platform;

import icbm.sentry.turret.block.TileTurret;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.CompatibilityModule;
import universalelectricity.api.electricity.IVoltageInput;
import universalelectricity.api.energy.IEnergyInterface;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.tile.TileExternalInventory;

/** Turret Platform
 * 
 * @author DarkGuardsman */
public class TileTurretPlatform extends TileExternalInventory implements IEnergyInterface, IVoltageInput
{
    private long voltage = 120;
    private TileTurret[] sentries = new TileTurret[6];

    public TileTurretPlatform()
    {
        this.maxSlots = 20;
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
                if (this.getStackInSlot(i) != null && CompatibilityModule.isHandler(this.getStackInSlot(i).getClass()))
                {
                    long charge = CompatibilityModule.dischargeItem(this.getStackInSlot(i), Integer.MAX_VALUE, false);
                    CompatibilityModule.dischargeItem(this.getStackInSlot(i), this.onReceiveEnergy(ForgeDirection.UNKNOWN, charge, true), true);
                }
            }
        }
    }

    public void refresh()
    {
        this.sentries = new TileTurret[6];
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            TileEntity ent = new Vector3(this).translate(direction).getTileEntity(this.worldObj);
            if (ent instanceof TileTurret)
            {
                this.sentries[direction.ordinal()] = (TileTurret) ent;
                if (ent instanceof IVoltageInput && ((IVoltageInput) ent).getVoltageInput(direction.getOpposite()) > voltage)
                {
                    voltage = ((IVoltageInput) ent).getVoltageInput(direction.getOpposite());
                }
            }
        }
    }

    @Override
    public long getVoltageInput(ForgeDirection direction)
    {
        return this.voltage;
    }

    @Override
    public void onWrongVoltage(ForgeDirection direction, long voltage)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return true;
    }

    @Override
    public long onReceiveEnergy(ForgeDirection from, long receive, boolean doReceive)
    {
        long actual = 0;
        long left = receive;
        for (int i = 0; i < 6; i++)
        {
            TileTurret sentry = this.sentries[i];
            if (CompatibilityModule.isHandler(sentry))
            {
                long in = CompatibilityModule.receiveEnergy(sentry, ForgeDirection.getOrientation(i).getOpposite(), left, doReceive);
                actual += in;
                left -= in;
            }
        }
        return actual;
    }

    @Override
    public long onExtractEnergy(ForgeDirection from, long extract, boolean doExtract)
    {
        return 0;
    }
}