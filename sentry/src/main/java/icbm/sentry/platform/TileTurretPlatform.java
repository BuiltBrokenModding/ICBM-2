package icbm.sentry.platform;

import icbm.sentry.turret.block.TileTurret;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.CompatibilityModule;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.tile.TileElectricalInventory;

/** Turret Platform
 * 
 * @author DarkGuardsman */
public class TileTurretPlatform extends TileElectricalInventory
{
    private TileTurret[] turrets = new TileTurret[6];

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
                if (this.getStackInSlot(i) != null && CompatibilityModule.isHandler(this.getStackInSlot(i).getClass()))
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
	public long getEnergy(ForgeDirection from)
	{
		return super.getEnergy(from);
	}

	@Override
	public long getEnergyCapacity(ForgeDirection from)
	{
		return super.getEnergyCapacity(from);
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
	public void setEnergyHandler(EnergyStorageHandler energy)
	{
		super.setEnergyHandler(energy);
	}

	@Override
    public long onExtractEnergy(ForgeDirection from, long extract, boolean doExtract)
    {
        return 0;
    }
}