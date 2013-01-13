package universalelectricity.core.implement;

/**
 * Applies to all objects that has a voltage.
 * 
 * @author Calclavia
 * 
 */
public interface IVoltage
{
	/**
	 * Gets the voltage of this object.
	 * 
	 * @param data - The data, possibly an ItemStack if this is an electric item.
	 * @return The amount of volts. E.g 120v or 240v
	 */
	public double getVoltage(Object... data);
}
