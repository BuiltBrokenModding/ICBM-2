package icbm.common.jiqi;

import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;

public abstract class TJiQiPao extends TileEntityElectricityReceiver
{
	public double prevDian;
	public double dian = 0;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		this.prevDian = this.dian;

		ElectricityPack electricityPack = ElectricityNetwork.consumeFromMultipleSides(this, new ElectricityPack(this.getWattRequest() / this.getVoltage(), this.getVoltage()));

		if (UniversalElectricity.isVoltageSensitive)
		{
			if (electricityPack.voltage > this.getVoltage())
			{
				this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 2f, true);
				return;
			}
		}

		this.dian = Math.ceil(this.dian + electricityPack.getWatts());
	}

	public abstract double getWattRequest();
}
