package icbm.sentry.interfaces;

/** Applied to turret that need energy to function
 * 
 * @author DarkGuardsman */
public interface IEnergyTurret
{
    /** Energy to function per tick */
    public long getJoulesPerTick();

    /** Gets max energy the turret can have */
    public long getEnergyCapacity();
}
