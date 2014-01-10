package icbm.api.sentry;

/**
 * @author Calclavia
 * 
 */
public interface IWeaponPlatform
{

	/**
	 * @return
	 */
	IWeaponSystem[] getWeaponSystems();

	/**
	 * @param slot
	 * @param system
	 * @return
	 */
	boolean canSupportWeaponSystem(int slot, IWeaponSystem system);

	/**
	 * @param slot
	 * @param system
	 * @return
	 */
	boolean addWeaponSystem(int slot, IWeaponSystem system);

	/**
	 * @param slot
	 * @param system
	 * @return
	 */
	boolean removeWeaponSystem(int slot, IWeaponSystem system);

}
