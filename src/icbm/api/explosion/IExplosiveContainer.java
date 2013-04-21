package icbm.api.explosion;

/**
 * An object that contains a reference to IExplosive. Carried by explosives, grenades and missile
 * entities etc.
 * 
 * @author Calclavia
 * 
 */
public interface IExplosiveContainer
{
	public IExplosive getExplosiveType();
}
