package icbm.sentry.platform;

import cpw.mods.fml.common.FMLCommonHandler;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.CompatibilityModule;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.tile.TileElectricalInventory;

/**
 * Turret Platform
 * 
 * @author DarkGuardsman
 */
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

		// TODO: Not good idea to constantly check.
		turrets = new TileTurret[6];

		for (int i = 0; i < 6; i++)
		{
			TileEntity checkTile = new Vector3(this).translate(ForgeDirection.getOrientation(i)).getTileEntity(worldObj);

			if (checkTile instanceof TileTurret)
				turrets[i] = (TileTurret) checkTile;
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

			if (turret != null && turret.getTurret() != null)
			{
				long added = turret.getTurret().energy.receiveEnergy(remain, doReceive);
				used += added;
				remain -= added;
			}
		}

		return used;
	}

	@Override
	public long onExtractEnergy(ForgeDirection from, long extract, boolean doExtract)
	{
		return 0;
	}
}