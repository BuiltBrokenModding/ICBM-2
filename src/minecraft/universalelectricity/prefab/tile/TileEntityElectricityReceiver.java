package universalelectricity.prefab.tile;

import java.util.EnumSet;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.Electricity;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.implement.IVoltage;

/**
 * An easier way to implement the methods from IElectricityReceiver with default values set.
 * 
 * @author Calclavia
 */
public abstract class TileEntityElectricityReceiver extends TileEntityDisableable implements IVoltage
{
	public TileEntityElectricityReceiver()
	{
		super();
		ElectricityConnections.registerConnector(this, EnumSet.range(ForgeDirection.DOWN, ForgeDirection.EAST));
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
	}

	@Override
	public double getVoltage(Object... data)
	{
		return 120;
	}

	@Override
	public void invalidate()
	{
		Electricity.instance.unregister(this);
		super.invalidate();

	}
}
