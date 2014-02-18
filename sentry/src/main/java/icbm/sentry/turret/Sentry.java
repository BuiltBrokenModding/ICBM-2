package icbm.sentry.turret;

import calclavia.lib.utility.nbt.SaveManager;
import icbm.sentry.interfaces.ISentry;
import icbm.sentry.interfaces.ISentryContainer;
import icbm.sentry.turret.block.TileTurret;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.vector.Vector3;

/** Modular way to deal with sentry guns
 * 
 * @author DarkGuardsman, tgame14 */
public abstract class Sentry implements IEnergyContainer, ISentry
{
    //TODO: implement a property system used by MC entities to support any number of settings a sentry can have
    protected float maxHealth = -1;
    public ISentryContainer host;
    protected Vector3 aimOffset;
    protected Vector3 centerOffset;
    protected float health;
    protected EnergyStorageHandler energy;
    protected double range;

    public Sentry(TileTurret host)
    {
        this.aimOffset = new Vector3(1, 0, 0);
        this.centerOffset = new Vector3(0, 0.5, 0);
        this.host = host;
        this.energy = new EnergyStorageHandler(1000);
        this.health = 0;
        this.range = 32.0;
    }

    public float getMaxHealth()
    {
        return maxHealth;
    }

    public void updateEntity()
    {

    }

    public boolean canFire()
    {
        return true;
    }

    public boolean fireWeapon(Vector3 target)
    {
        if (world().isRemote)
        {
            fireWeaponClient(target);
        }
        else
        {
            //Call weapon system
        }
        return false;
    }

    /** visual rendering here */
    public void fireWeaponClient(Vector3 target)
    {
        // TODO Auto-generated method stub

    }

    /** Offset from the center offset to were the end of the barrel should be at */
    public Vector3 getAimOffset()
    {
        return this.aimOffset;
    }

    /** Offset from host location to were the sentries center is located */
    public Vector3 getCenterOffset()
    {
        return this.centerOffset;
    }

    public float getHealth()
    {
        return this.health;
    }

    @Override
    public void setEnergy(ForgeDirection dir, long energy)
    {
        this.energy.setEnergy(energy);
    }

    @Override
    public long getEnergy(ForgeDirection dir)
    {
        return this.energy.getEnergy();
    }

    @Override
    public long getEnergyCapacity(ForgeDirection dir)
    {
        return this.energy.getEnergyCapacity();
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        nbt.setString(ISentry.SENTRY_SAVE_ID, SaveManager.getID(this.getClass()));
        if (this.energy != null)
            this.energy.writeToNBT(nbt);

    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (this.energy != null)
            this.energy.readFromNBT(nbt);

    }

    public ISentryContainer getHost()
    {
        return this.host;
    }

    public World world()
    {
        return this.getHost().world();
    }

    public double x()
    {
        return this.getHost().x();
    }

    public double y()
    {
        return this.getHost().y();
    }

    public double z()
    {
        return this.getHost().z();
    }

    @Override
    public boolean automated()
    {
        return false;
    }

    @Override
    public boolean mountable()
    {
        return false;
    }

    @Override
    public String toString()
    {
        String id = SentryRegistry.getKeyForSentry(this);
        return "[Sentry]ID: " + (id != null ? id : "unknown") + "   " + super.toString();
    }
}
