package icbm.sentry.turret.sentries;

import icbm.api.sentry.ISentry;
import icbm.api.sentry.IWeaponSystem;
import icbm.sentry.turret.TileTurret;
import universalelectricity.api.vector.Vector3;

/** Module way to deal with sentry guns
 * 
 * @author DarkGuardsman */
public class Sentry implements ISentry
{
    private IWeaponSystem[] weaponSystems = new IWeaponSystem[1];
    protected int health = 100;
    protected long energyPerTick = 1;
    protected long energyMax = 1000;
    protected long voltage = 240;
    protected Vector3 aimOffset = new Vector3(1, 0, 0);
    protected Vector3 getCenterOffset = new Vector3(0.5, 0.5, 0.5);

    protected TileTurret turret;

    public Sentry(TileTurret turret)
    {
        this.turret = turret;
    }

    @Override
    public IWeaponSystem[] getWeaponSystems()
    {
        return this.weaponSystems;
    }

    @Override
    public boolean canSupportWeaponSystem(int slot, IWeaponSystem system)
    {
        return false;
    }

    @Override
    public boolean addWeaponSystem(int slot, IWeaponSystem system)
    {
        return false;
    }

    @Override
    public boolean removeWeaponSystem(int slot, IWeaponSystem system)
    {
        return false;
    }

    public int getMaxHp()
    {
        return this.health;
    }

    public long getEnergyPerTick()
    {
        return this.energyPerTick;
    }

    public long getEnergyCapacity()
    {
        return this.energyMax;
    }

    public long voltage()
    {
        return this.voltage;
    }

    public void playFiringSound()
    {

    }

    /** Offset from center of were the barrel ends */
    public Vector3 getAimOffset()
    {
        return this.aimOffset;
    }

    /** Offset from the block's corrds were the center actualy is */
    public Vector3 getCenterOffset()
    {
        return this.getCenterOffset;
    }
}
