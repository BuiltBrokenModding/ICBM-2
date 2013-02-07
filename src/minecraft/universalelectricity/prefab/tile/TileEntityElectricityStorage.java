package universalelectricity.prefab.tile;

import java.util.EnumSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.implement.IJouleStorage;

public abstract class TileEntityElectricityStorage extends TileEntityElectricityReceiver implements IJouleStorage
{
	/**
	 * The amount of joules stored within this machine. Use get and set functions instead of
	 * referencing to this variable.
	 */
	private double joules = 0;

	public double prevJoules = 0;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		this.prevJoules = joules;

		if (!this.worldObj.isRemote)
		{
			if (!this.isDisabled())
			{
				ElectricityPack electricityPack = ElectricityNetwork.consumeFromMultipleSides(this, this.getConsumingSides(), this.getRequest());
				this.onReceive(electricityPack);
			}
			else
			{
				ElectricityNetwork.consumeFromMultipleSides(this, new ElectricityPack());
			}
		}

	}

	/**
	 * The sides in which this machine can consume electricity from.
	 */
	protected EnumSet<ForgeDirection> getConsumingSides()
	{
		return ElectricityConnections.getDirections(this);
	}

	/**
	 * Returns the amount of energy being requested this tick. Return an empty ElectricityPack if no
	 * electricity is desired.
	 */
	public ElectricityPack getRequest()
	{
		return new ElectricityPack((this.getMaxJoules() - this.getJoules()) / this.getVoltage(), this.getVoltage());
	}

	/**
	 * Called right after electricity is transmitted to the TileEntity. Override this if you wish to
	 * have another effect for a voltage overcharge.
	 * 
	 * @param electricityPack
	 */
	public void onReceive(ElectricityPack electricityPack)
	{
		/**
		 * Creates an explosion if the voltage is too high.
		 */
		if (UniversalElectricity.isVoltageSensitive)
		{
			if (electricityPack.voltage > this.getVoltage())
			{
				this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 1.5f, true);
				return;
			}
		}

		this.setJoules(this.getJoules() + electricityPack.getWatts());
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		this.joules = par1NBTTagCompound.getDouble("joules");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setDouble("joules", this.joules);
	}

	@Override
	public double getJoules(Object... data)
	{
		return this.joules;
	}

	@Override
	public void setJoules(double joules, Object... data)
	{
		this.joules = Math.max(Math.min(joules, this.getMaxJoules()), 0);
	}
}