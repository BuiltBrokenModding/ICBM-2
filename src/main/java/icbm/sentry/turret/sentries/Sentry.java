package icbm.sentry.turret.sentries;

import universalelectricity.api.vector.Vector3;
import icbm.api.sentry.IWeaponPlatform;
import icbm.api.sentry.IWeaponSystem;
import icbm.sentry.turret.TileTurret;

/** Module way to deal with sentry guns
 * 
 * @author DarkGuardsman */
public class Sentry implements IWeaponPlatform
{
    private IWeaponSystem[] weaponSystems = new IWeaponSystem[1];
    protected int health = 100;
    protected int ammoSlots = 4;
    protected long energyPerTick = 1;
    protected long energyMax = 1000;
    protected long voltage = 240;
    protected Vector3 aimOffset;

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

    public int getAmmoSlots()
    {
        return this.ammoSlots;
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

    public Vector3 getAimOffset()
    {
        return this.aimOffset;
    }
}
