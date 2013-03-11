package atomicscience.api;

/**
 * Applied to all TileEntities or Blocks that has a temperature value. This will allow the
 * temperature measurement tool to measure the block.
 * 
 * @author Calclavia
 * 
 */
public interface ITemperature
{
	/**
	 * Gets the temperature of this block in celsius.
	 */
	public float getTemperature();

	public void setTemperature(float celsius);
}
