package icbm.common.jiqi;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.implement.IJouleStorage;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;

public abstract class TJiQiCun extends TileEntityElectricityReceiver implements IJouleStorage
{
	private double dian = 0;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			boolean didFind = false;

			for (int i = 0; i < 6; i++)
			{
				Vector3 diDian = new Vector3(this);
				diDian.modifyPositionFromSide(ForgeDirection.getOrientation(i));
				TileEntity tileEntity = diDian.getTileEntity(this.worldObj);
				ElectricityNetwork inputNetwork = ElectricityNetwork.getNetworkFromTileEntity(tileEntity, ForgeDirection.getOrientation(i));

				if (inputNetwork != null)
				{
					if (!this.isDisabled() && this.getJoules() < this.getMaxJoules() && !didFind)
					{
						inputNetwork.startRequesting(this, (this.getMaxJoules() - this.dian) / this.getVoltage(), this.getVoltage());
						ElectricityPack electricityPack = inputNetwork.consumeElectricity(this);

						if (UniversalElectricity.isVoltageSensitive)
						{
							if (electricityPack.voltage > this.getVoltage())
							{
								this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 2f, true);
								return;
							}
						}

						this.setJoules(Math.ceil(this.dian + electricityPack.getWatts()));
						didFind = true;
					}
					else
					{
						inputNetwork.stopRequesting(this);
					}
				}
			}
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.dian = par1NBTTagCompound.getDouble("dian");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setDouble("dian", this.dian);
	}

	@Override
	public double getJoules(Object... data)
	{
		return this.dian;
	}

	@Override
	public void setJoules(double joules, Object... data)
	{
		this.dian = Math.max(Math.min(joules, this.getMaxJoules()), 0);
	}
}
