package icbm.sentry.turret;

import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.ai.EulerServo;
import icbm.sentry.turret.ai.TurretAI;
import icbm.sentry.turret.weapon.IEnergyWeapon;
import icbm.sentry.turret.weapon.WeaponSystem;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;
import calclavia.lib.utility.nbt.SaveManager;

/** Modular way to deal with sentry guns
 * 
 * @author DarkGuardsman, tgame14 */
public abstract class Turret implements IEnergyContainer, ITurret
{
    /** TODO: Implement a property system used by MC entities to support any number of settings a
     * turrets can have. Turret object references. */
    public final ITurretProvider host;
    private EulerServo servo;
    private TurretAI ai;
    public EnergyStorageHandler energy;

    /** Turret Attributes TODO: change out weapon system var for an interface and registry system */
    protected WeaponSystem weaponSystem;
    protected Vector3 aimOffset = new Vector3();
    protected Vector3 centerOffset = new Vector3();
    protected float barrelLength = 1;
    protected float maxHealth = -1;
    protected float health;
    protected int range = 10;
    protected int maxCooldown = 20;
    protected int cooldown = 0;

    public Turret(ITurretProvider host)
    {
        this.host = host;
        energy = new EnergyStorageHandler(10000);
        this.ai = new TurretAI(this);
    }

    public float getMaxHealth()
    {
        return maxHealth;
    }

    public void update()
    {
        if (cooldown > 0)
            cooldown--;
    }

    public boolean canFire()
    {
        if (this.weaponSystem instanceof IEnergyWeapon)
        {
            if(energy.extractEnergy(((IEnergyWeapon) this.weaponSystem).getEnergyPerShot(), false) < ((IEnergyWeapon) this.weaponSystem).getEnergyPerShot())
            {
                return false;
            }
        }
        return cooldown == 0 && weaponSystem.canFire();
    }

    @Override
    public boolean fire(Vector3 target)
    {
        if (getHost().world().isRemote)
        {
            weaponSystem.fireClient(target);
            return true;
        }
        else if (canFire())
        {
            if (target != null)
                getHost().sendFireEventToClient(target);
            weaponSystem.fire(target);
            if (this.weaponSystem instanceof IEnergyWeapon)
            {
                energy.extractEnergy(((IEnergyWeapon) this.weaponSystem).getEnergyPerShot(), true);
            }
            cooldown = maxCooldown;
            return true;
        }

        return false;
    }

    @Override
    public boolean fire(Entity target)
    {
        if (getHost().world().isRemote)
        {
            weaponSystem.fireClient(Vector3.fromCenter(target));
            return true;
        }
        else if (canFire())
        {
            if (target != null)
                getHost().sendFireEventToClient(Vector3.fromCenter(target));
            weaponSystem.fire(target);
            if (this.weaponSystem instanceof IEnergyWeapon)
            {
                energy.extractEnergy(((IEnergyWeapon) this.weaponSystem).getEnergyPerShot(), true);
            }
            cooldown = maxCooldown;
            return true;
        }
        return false;
    }

    /** Offset from the center offset to were the end of the barrel should be at. This is RELATIVE to
     * the center! */
    public Vector3 getAimOffset()
    {
        return new Vector3(getServo()).scale(barrelLength).translate(aimOffset);
    }

    /** Offset from host location to were the sentries center is located */
    public Vector3 getCenterOffset()
    {
        return centerOffset.clone();
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
        nbt.setString(ITurret.SENTRY_SAVE_ID, SaveManager.getID(this.getClass()));
        if (this.energy != null)
            this.energy.writeToNBT(nbt);
        nbt.setDouble("yaw", getServo().yaw);
        nbt.setDouble("pitch", getServo().pitch);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (this.energy != null)
            this.energy.readFromNBT(nbt);
        getServo().yaw = nbt.getDouble("yaw");
        getServo().pitch = nbt.getDouble("pitch");
        getServo().hasChanged = true;
    }

    @Override
    public ITurretProvider getHost()
    {
        return host;
    }

    public World world()
    {
        return getHost().world();
    }

    public double x()
    {
        return getHost().x();
    }

    public double y()
    {
        return getHost().y();
    }

    public double z()
    {
        return getHost().z();
    }

    public VectorWorld getPosition()
    {
        return new VectorWorld(world(), x(), y(), z());
    }

    public VectorWorld getAbsoluteCenter()
    {
        return (VectorWorld) getPosition().translate(getCenterOffset());
    }

    public EulerServo getServo()
    {
        if (servo == null)
        {
            servo = new EulerServo(5);
        }

        return servo;
    }

    @Override
    public boolean mountable()
    {
        return false;
    }

    @Override
    public int getRange()
    {
        return this.range;
    }

    @Override
    public String toString()
    {
        String id = TurretRegistry.getID(this);
        return "[Turret] ID: " + (id != null ? id : "unknown") + "   " + super.toString();
    }

    public TurretAI getAi()
    {
        return ai;
    }
}
