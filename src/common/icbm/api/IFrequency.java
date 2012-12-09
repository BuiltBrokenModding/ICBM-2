package icbm.api;

/**
 * Applied to all things that has a frequency.
 * 
 * @author Calclavia
 */

public interface IFrequency
{
	/**
	 * @param data - Pass an ItemStack if dealing with items with frequencies.
	 * @return The frequency of this object.
	 */
	public short getFrequency(Object... data);

	/**
	 * Sets the frequency
	 * 
	 * @param frequency - The frequency of this object.
	 * @param data - Pass an ItemStack if dealing with items with frequencies.
	 */
	public void setFrequency(short frequency, Object... data);
}
