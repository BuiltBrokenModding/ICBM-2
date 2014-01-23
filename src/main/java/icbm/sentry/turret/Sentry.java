package icbm.sentry.turret;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.energy.IEntityEnergyContainer;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.utility.nbt.ISaveObj;

/** Module way to deal with sentry guns
 * 
 * @author DarkGuardsman */
public class Sentry implements IEntityEnergyContainer, ISaveObj
{
    protected Vector3 aimOffset = new Vector3(1, 0, 0);
    protected Vector3 centerOffset = new Vector3(0.5, 0.5, 0.5);
    protected TileSentry host;
    protected float health = 0;
    protected float maxHealth = -1;
    protected EnergyStorageHandler energy;

    public Sentry(TileSentry host)
    {
        this.host = host;
        this.energy = new EnergyStorageHandler(1000);
    }

    public void update()
    {

    }

    public boolean canFire()
    {
        return false;
    }

    public boolean fireWeapon(Vector3 target)
    {
        return false;
    }

    public void fireWeaponClient(Vector3 target)
    {
        // TODO Auto-generated method stub

    }

    public Vector3 getAimOffset()
    {
        return this.aimOffset;
    }

    public Vector3 getCenterOffset()
    {
        return this.centerOffset;
    }

    public float getHealth()
    {
        return this.health;
    }

    public float getMaxHealth()
    {
        return this.maxHealth;
    }

    @Override
    public void setEnergy(long energy)
    {
        this.energy.setEnergy(energy);
    }

    @Override
    public long getEnergy()
    {
        return this.energy.getEnergy();
    }

    @Override
    public long getEnergyCapacity()
    {
        return this.energy.getEnergyCapacity();
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        if (this.energy != null)
            this.energy.writeToNBT(nbt);
        if (this.maxHealth > 0)
            nbt.setFloat("Health", this.health);

    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (this.energy != null)
            this.energy.readFromNBT(nbt);
        this.health = nbt.getFloat("Health");

    }

}
