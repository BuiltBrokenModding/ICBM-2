package icbm.sentry.turret;

import icbm.sentry.interfaces.IEnergyWeapon;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.interfaces.ITurretUpgrade;
import icbm.sentry.interfaces.IWeaponProvider;
import icbm.sentry.interfaces.IWeaponSystem;
import icbm.sentry.turret.ai.EulerServo;
import icbm.sentry.turret.ai.TurretAI;
import icbm.sentry.turret.weapon.WeaponSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;
import calclavia.lib.utility.nbt.SaveManager;

/** Modular way to deal with sentry guns
 * 
 * @author DarkGuardsman, tgame14 */
public abstract class Turret implements IEnergyContainer, ITurret, IWeaponProvider
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
    protected double default_target_range = 30;
    protected double target_range = 30;
    protected int maxCooldown = 20;
    protected int cooldown = 0;
    protected long ticks = 0;

    public HashMap<String, Double> upgrade_count = new HashMap<String, Double>();
    public HashMap<String, Double> traits = new HashMap<String, Double>();

    public Turret(ITurretProvider host)
    {
        this.host = host;
        energy = new EnergyStorageHandler(10000);
        this.ai = new TurretAI(this);
    }

    public void init()
    {
        this.updateUpgrades();
    }

    public void update()
    {
        if (ticks == 0)
        {
            this.init();
        }
        ticks++;
        if (ticks >= Long.MAX_VALUE - 10)
        {
            ticks = 1;
        }

        if (cooldown > 0)
            cooldown--;
    }

    public boolean canFire()
    {
        if (this.getWeaponSystem() instanceof IEnergyWeapon)
        {
            if (energy.extractEnergy(((IEnergyWeapon) this.weaponSystem).getEnergyPerShot(), false) < ((IEnergyWeapon) this.weaponSystem).getEnergyPerShot())
            {
                return false;
            }
        }
        return cooldown == 0 && this.getWeaponSystem().canFire();
    }

    @Override
    public boolean fire(IVector3 target)
    {
        if (getHost().world().isRemote)
        {
            this.getWeaponSystem().fireClient(target);
            return true;
        }
        else if (canFire())
        {
            if (target != null)
                getHost().sendFireEventToClient(target);
            this.getWeaponSystem().fire(target);
            if (this.getWeaponSystem() instanceof IEnergyWeapon)
            {
                energy.extractEnergy(((IEnergyWeapon) this.getWeaponSystem()).getEnergyPerShot(), true);
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
            this.getWeaponSystem().fireClient(Vector3.fromCenter(target));
            return true;
        }
        else if (canFire())
        {
            if (target != null)
                getHost().sendFireEventToClient(Vector3.fromCenter(target));
            this.getWeaponSystem().fire(target);
            if (this.getWeaponSystem() instanceof IEnergyWeapon)
            {
                energy.extractEnergy(((IEnergyWeapon) this.getWeaponSystem()).getEnergyPerShot(), true);
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
        nbt.setString(ITurret.SENTRY_TYPE_SAVE_ID, SaveManager.getID(this.getClass()));
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

    @Override
    public World world()
    {
        return getHost().world();
    }

    @Override
    public double x()
    {
        return getHost().x();
    }

    @Override
    public double y()
    {
        return getHost().y();
    }

    @Override
    public double z()
    {
        return getHost().z();
    }

    @Override
    public double yaw()
    {
        return getServo().yaw;
    }

    @Override
    public double pitch()
    {
        return getServo().pitch;
    }

    @Override
    public double roll()
    {
        return getServo().roll;
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
    public String toString()
    {
        String id = TurretRegistry.getID(this);
        return "[Turret] ID: " + (id != null ? id : "unknown") + "   " + super.toString();
    }

    public TurretAI getAi()
    {
        return ai;
    }

    @Override
    public IWeaponSystem getWeaponSystem()
    {
        return this.weaponSystem;
    }

    public void updateUpgrades()
    {
        //TODO: change to only scan upgrade slots
        IInventory inv = this.getHost().getInventory();

        //Reset
        this.upgrade_count.clear();
        this.target_range = this.default_target_range;

        //Update upgrade count
        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            if (inv.getStackInSlot(slot) != null && inv.getStackInSlot(slot).getItem() instanceof ITurretUpgrade)
            {
                final List<String> id_list = new ArrayList<String>();
                ((ITurretUpgrade) inv.getStackInSlot(slot).getItem()).getTypes(id_list, inv.getStackInSlot(slot));
                for (String id : id_list)
                {
                    if (!this.upgrade_count.containsKey(id))
                    {
                        this.upgrade_count.put(id, 0.0D);
                    }
                    this.upgrade_count.put(id, this.upgrade_count.get(id) + ((ITurretUpgrade) inv.getStackInSlot(slot).getItem()).getUpgradeEfficiance(inv.getStackInSlot(slot), id));
                }
            }
        }

        //Apply upgrades to traits
        if (this.upgrade_count.containsKey(ITurretUpgrade.TARGET_RANGE))
        {
            this.target_range = this.default_target_range + (this.default_target_range * this.upgrade_count.get(ITurretUpgrade.TARGET_RANGE));
        }
    }

    @Override
    public HashMap<String, Double> upgrades()
    {
        return this.upgrade_count;
    }

    @Override
    public HashMap<String, Double> traits()
    {
        return this.traits;
    }
}
