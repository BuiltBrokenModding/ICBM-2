package icbm.common.jiqi;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.implement.IJouleStorage;
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
			ElectricityPack electricityPack = ElectricityNetwork.consumeFromMultipleSides(this, new ElectricityPack((this.getMaxJoules() - this.dian) / this.getVoltage(), this.getVoltage()));

			if (UniversalElectricity.isVoltageSensitive)
			{
				if (electricityPack.voltage > this.getVoltage())
				{
					this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 2f, true);
					return;
				}
			}

			this.setJoules(Math.ceil(this.getJoules() + electricityPack.getWatts()));
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
