package icbm.api.explosion;

/**
 * An interface used to find various types of explosive's information.
 * 
 * @author Calclavia
 * 
 */
public interface IExplosive
{
	/**
	 * @return Gets the explosive's ID.
	 */
	public int getID();

	/**
	 * @return The name key in the ICBM language file.
	 */
	public String getUnlocalizedName();

	/**
	 * @return Gets the specific translated name of the following versions of the explosive.
	 */
	public String getExplosiveName();

	public String getGrenadeName();

	public String getMissileName();

	public String getMinecartName();

	/**
	 * @return The radius of effect of the explosion.
	 */
	public float getRadius();

	/**
	 * @return The tier of the explosive.
	 */
	public int getTier();

	/**
	 * @return The energy emitted by this explosive. In Joules.
	 */
	public double getEnergy();
}
