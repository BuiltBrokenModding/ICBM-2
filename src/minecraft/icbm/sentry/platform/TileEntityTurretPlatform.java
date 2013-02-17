package icbm.sentry.platform;

import icbm.sentry.api.IAmmunition;
import icbm.sentry.terminal.TileEntityTerminal;
import icbm.sentry.turret.TileEntityBaseTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.TranslationHelper;

public class TileEntityTurretPlatform extends TileEntityTerminal implements IAmmunition
{
	/**
	 * The turret linked to this platform.
	 */
	private TileEntityBaseTurret turret = null;
	public ForgeDirection deployDirection = ForgeDirection.UP;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.isRunning())
		{
			this.getTurret();
		}
	}

	@Override
	public void onReceive(ElectricityPack electricityPack)
	{
		/**
		 * Creates an explosion if the voltage is too high.
		 */
		if (UniversalElectricity.isVoltageSensitive)
		{
			if (electricityPack.voltage > this.getVoltage())
			{
				/**
				 * Since this block is indestructable, we have to delete it.
				 */
				this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);
				this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 1.5f, true);
				return;
			}
		}

		this.wattsReceived = Math.min(this.wattsReceived + electricityPack.getWatts(), this.getWattBuffer());
	}

	@Override
	public ElectricityPack getRequest()
	{
		if (this.getTurret() != null)
		{
			if (this.wattsReceived < this.getTurret().getRequest())
			{
				return new ElectricityPack(this.getTurret().getRequest() / this.getTurret().getVoltage(), this.getTurret().getVoltage());
			}
		}

		return new ElectricityPack();
	}

	@Override
	public double getWattBuffer()
	{
		if (this.getTurret() != null)
		{
			return this.getTurret().getRequest();
		}
		return 0;
	}

	public TileEntityBaseTurret getTurret()
	{
		TileEntity tileEntity = this.worldObj.getBlockTileEntity(this.xCoord + this.deployDirection.offsetX, this.yCoord + this.deployDirection.offsetY, this.zCoord + this.deployDirection.offsetZ);

		if (tileEntity instanceof TileEntityBaseTurret)
		{
			this.turret = (TileEntityBaseTurret) tileEntity;
		}
		else
		{
			this.turret = null;
		}

		return this.turret;
	}

	/**
	 * if a sentry is spawned above the stand it is removed
	 * 
	 * @return
	 */
	public boolean removeTurret()
	{
		TileEntity ent = worldObj.getBlockTileEntity(xCoord + deployDirection.offsetX, yCoord + deployDirection.offsetY, zCoord + deployDirection.offsetZ);
		if (ent instanceof TileEntityBaseTurret)
		{
			this.turret = null;
			return ((TileEntityBaseTurret) ent).destroy(false);
		}
		return false;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		if (side != ForgeDirection.DOWN && side != ForgeDirection.UP)
		{
			return 0;
		}
		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		if (side != ForgeDirection.DOWN && side != ForgeDirection.UP)
		{
			return 15;
		}
		return 0;
	}

	@Override
	public String getInvName()
	{
		return TranslationHelper.getLocal("tile.turretPlatform.name");
	}

	public boolean isRunning()
	{
		return !this.isDisabled();
	}

	@Override
	public boolean hasAmmunition(ItemStack ammunitionStack)
	{
		for (int i = 0; i < this.UPGRADE_START_INDEX; i++)
		{
			ItemStack itemStack = this.containingItems[i];

			if (itemStack != null)
			{
				if (itemStack.isItemEqual(ammunitionStack))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean useAmmunition(ItemStack ammunitionStack)
	{
		for (int i = 0; i < this.UPGRADE_START_INDEX; i++)
		{
			ItemStack itemStack = this.containingItems[i];

			if (itemStack != null)
			{
				if (itemStack.isItemEqual(ammunitionStack))
				{
					this.decrStackSize(i, 1);
					return true;
				}
			}
		}
		return false;
	}
}