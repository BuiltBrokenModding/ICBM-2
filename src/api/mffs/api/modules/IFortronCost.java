package mffs.api.modules;

/**
 * @author Calclavia
 * 
 */
public interface IFortronCost
{
	/**
	 * The amount of Fortron this module consumes per tick.
	 * 
	 * @return
	 */
	public float getFortronCost(float amplifier);
}
