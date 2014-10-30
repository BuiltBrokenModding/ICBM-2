package icbm.sentry.interfaces;


import resonant.api.electric.IEnergyContainer;

/** Applied to turret that need energy to function
 * 
 * @author DarkGuardsman */
public interface IEnergyTurret extends IEnergyContainer, ITurret
{
    /** Energy to function per tick */
    public long getRunningCost();
}
