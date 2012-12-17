package universalelectricity.core.electricity;

/**
 * A simple way to store electrical data.
 * 
 * @author Calclavia
 * 
 */
public class ElectricityPack implements Cloneable
{
	public double amperes;
	public double voltage;

	public ElectricityPack(double amperes, double voltage)
	{
		this.amperes = amperes;
		this.voltage = voltage;
	}

	public ElectricityPack()
	{
		this(0, 0);
	}

	public double getWatts()
	{
		return ElectricInfo.getWatts(amperes, voltage);
	}

	public double getConductance()
	{
		return ElectricInfo.getConductance(amperes, voltage);
	}

	public double getResistance()
	{
		return ElectricInfo.getResistance(amperes, voltage);
	}

	@Override
	public String toString()
	{
		return "ElectricityPack [Amps:" + this.amperes + " Volts:" + this.voltage + "]";
	}

	@Override
	public ElectricityPack clone()
	{
		return new ElectricityPack(this.amperes, this.voltage);
	}

	public boolean isEquals(ElectricityPack electricityPack)
	{
		return this.amperes == electricityPack.amperes && this.voltage == electricityPack.voltage;
	}
}
