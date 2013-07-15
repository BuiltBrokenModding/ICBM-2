package atomicscience.api;

public interface IFissileMaterial
{
	/**
	 * @return 0 - Damage, 1 - Do nothing, 2 - Undamage.
	 */
	public int onFissile(ITemperature reactor);
}
