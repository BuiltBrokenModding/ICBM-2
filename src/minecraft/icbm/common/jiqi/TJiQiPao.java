package icbm.common.jiqi;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
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
					if (!this.isDisabled() && !didFind)
					{
						inputNetwork.startRequesting(this, this.getWattRequest() / this.getVoltage(), this.getVoltage());
						ElectricityPack electricityPack = inputNetwork.consumeElectricity(this);

						if (UniversalElectricity.isVoltageSensitive)
						{
							if (electricityPack.voltage > this.getVoltage())
							{
								this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 2f, true);
								return;
							}
						}

						this.dian = Math.ceil(this.dian + electricityPack.getWatts());

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

	public abstract double getWattRequest();
}
