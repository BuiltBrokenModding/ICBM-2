package universalelectricity.prefab.tile;

import java.util.EnumSet;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.ElectricityPack;

/**
 * This class should be extended by TileEntities that run on electricity but do not store them.
 * Things such as electric furnaces should extend this. Take this class mainly as an example.
 * 
 * @author Calclavia
 * 
 */
public abstract class TileEntityElectricityRunnable extends TileEntityElectricityReceiver
{
	/**
	 * The amount of watts received this tick. This variable should be deducted when used.
	 */
	public double prevWatts, wattsReceived = 0;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		this.prevWatts = this.wattsReceived;

		/**
		 * ElectricityManager works on server side.
		 */
		if (!this.worldObj.isRemote)
		{
			/**
			 * If the machine is disabled, stop requesting electricity.
			 */
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
	 * Returns the amount of energy being requested this tick. Return an empty ElectricityPack if no
	 * electricity is desired.
	 */
	public ElectricityPack getRequest()
	{
		return new ElectricityPack();
	}

	/**
	 * The sides in which this machine can consume electricity from.
	 */
	protected EnumSet<ForgeDirection> getConsumingSides()
	{
		return ElectricityConnections.getDirections(this);
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

		this.wattsReceived = Math.min(this.wattsReceived + electricityPack.getWatts(), this.getWattBuffer());
	}

	/**
	 * @return The amount of internal buffer that may be stored within this machine. This will make
	 * the machine run smoother as electricity might not always be consistent.
	 */
	public double getWattBuffer()
	{
		return this.getRequest().getWatts() * 2;
	}
}