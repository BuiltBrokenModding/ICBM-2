package icbm.sentry.interfaces;

/** Applied to weapon systems that need energy
 * 
 * @author DarkGuardsman */
public interface IEnergyWeapon extends IWeaponSystem
{
    /** Energy cost per shot */
    public long getEnergyPerShot();
}
