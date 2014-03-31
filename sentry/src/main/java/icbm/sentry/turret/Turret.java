package icbm.sentry.turret;

import icbm.sentry.interfaces.IEnergyWeapon;
import icbm.sentry.interfaces.IKillCount;
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
public abstract class Turret implements IEnergyContainer, ITurret, IWeaponProvider, IKillCount
{
    public final ITurretProvider host;
    private final HashMap<String, Double> upgrade_count = new HashMap<String, Double>();
    private final HashMap<String, Double> traits_default = new HashMap<String, Double>();
    private final HashMap<String, Double> traits = new HashMap<String, Double>();
    public EnergyStorageHandler battery;
    /** Turret Attributes TODO: change out weapon system var for an interface and registry system */
    protected WeaponSystem weaponSystem;
    protected Vector3 aimOffset = new Vector3();
    protected Vector3 centerOffset = new Vector3();
    protected float barrelLength = 1;
    protected int maxCooldown = 20;
    protected int cooldown = 0;
    protected long ticks = 0;
    private EulerServo servo;
    private TurretAI ai;

    public Turret(ITurretProvider host)
    {
        this.host = host;
        this.ai = new TurretAI(this);
        this.battery = new EnergyStorageHandler()
        {
            @Override
            public long getEnergyCapacity()
            {
                if (Turret.this.traits.containsKey(ITurret.ENERGY_STORAGE_TRAIT))
                {
                    return (int) (Turret.this.traits.get(ITurret.ENERGY_STORAGE_TRAIT) * 1);
                }
                return this.capacity;
            }

            @Override
            public long getMaxExtract()
            {
                if (Turret.this.getWeaponSystem() instanceof IEnergyWeapon)
                {
                    return ((IEnergyWeapon) Turret.this.getWeaponSystem()).getEnergyPerShot();
                }
                return this.getEnergyCapacity();
            }
        };
    }

    public void init()
    {
        this.traits.putAll(traits_default);
        this.onInventoryChanged();
    }

    @Override
    public void update()
    {

        tick();
        if (cooldown > 0)
        {
            cooldown--;
        }
    }

    public void tick()
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
    }

    /** Applies a trait to a sentry, prevents addition after first update */
    public void applyTrait(String name, double value)
    {
        if (this.ticks == 0)
        {
            this.traits_default.put(name, value);
        }
    }

    public boolean canFire()
    {
        boolean cooldown_flag = cooldown <= 0;
        boolean fire_flag = this.getWeaponSystem().canFire();
        if (this.getWeaponSystem() instanceof IEnergyWeapon)
        {
            if (battery.extractEnergy(((IEnergyWeapon) this.getWeaponSystem()).getEnergyPerShot(), false) < ((IEnergyWeapon) this.getWeaponSystem()).getEnergyPerShot())
            {
                return false;
            }
        }

        return cooldown_flag && fire_flag;
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
            {
                getHost().sendFireEventToClient(target);
            }
            this.getWeaponSystem().fire(target);
            if (this.getWeaponSystem() instanceof IEnergyWeapon)
            {
                battery.extractEnergy(((IEnergyWeapon) this.getWeaponSystem()).getEnergyPerShot(), true);
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
            {
                getHost().sendFireEventToClient(Vector3.fromCenter(target));
            }
            this.getWeaponSystem().fire(target);
            if (this.getWeaponSystem() instanceof IEnergyWeapon)
            {
                battery.extractEnergy(((IEnergyWeapon) this.getWeaponSystem()).getEnergyPerShot(), true);
            }
            cooldown = maxCooldown;
            return true;
        }
        return false;
    }

    /** Offset from the center offset to were the end of the barrel should be at. This is RELATIVE to
     * the center! */
    @Override
    public Vector3 getWeaponOffset()
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
        this.battery.setEnergy(energy);
    }

    @Override
    public long getEnergy(ForgeDirection dir)
    {
        return this.battery.getEnergy();
    }

    @Override
    public long getEnergyCapacity(ForgeDirection dir)
    {
        return this.battery.getEnergyCapacity();
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        nbt.setString(ITurret.SENTRY_TYPE_SAVE_ID, SaveManager.getID(this.getClass()));
        if (this.battery != null)
        {
            this.battery.writeToNBT(nbt);
        }
        nbt.setDouble("yaw", getServo().yaw);
        nbt.setDouble("pitch", getServo().pitch);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (this.battery != null)
        {
            this.battery.readFromNBT(nbt);
        }
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

    @Override
    public VectorWorld fromCenter()
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

    @Override
    public void onSettingsChanged()
    {
        //TODO when GUI settings are implemented update sentry traits here
        //TODO when range settings are implemented confuse the sentry if max_limit range is set higher than AI max range.
        //Possible effect for confusion are targeting allies past he max range
    }

    @Override
    public void onInventoryChanged()
    {
        //TODO: change to only scan upgrade slots
        IInventory inv = this.getHost().getInventory();

        //Reset
        this.upgrade_count.clear();
        this.traits.clear();
        this.traits.putAll(this.traits_default);

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
        if (this.upgrade_count.containsKey(ITurretUpgrade.TARGET_RANGE) && this.traits.containsKey(ITurret.SEARCH_RANGE_TRAIT))
        {
            double range = this.traits.get(ITurret.SEARCH_RANGE_TRAIT) + (this.traits.get(ITurret.SEARCH_RANGE_TRAIT) * this.upgrade_count.get(ITurretUpgrade.TARGET_RANGE));
            this.traits.put(ITurret.SEARCH_RANGE_TRAIT, range);
        }
        if (this.upgrade_count.containsKey(ITurretUpgrade.ENERGY_UPGRADE) && this.traits.containsKey(ITurret.ENERGY_RUNNING_TRAIT))
        {
            double energy = this.traits.get(ITurret.ENERGY_STORAGE_TRAIT) + (this.traits.get(ITurret.ENERGY_STORAGE_TRAIT) * this.upgrade_count.get(ITurretUpgrade.ENERGY_UPGRADE));
            this.traits.put(ITurret.ENERGY_STORAGE_TRAIT, energy);
        }
    }

    @Override
    public HashMap<String, Double> upgrades()
    {
        return this.upgrade_count;
    }

    @Override
    public double getUpgradeEffect(String upgrade)
    {
        if (this.upgrades().containsKey(upgrade))
        {
            return this.upgrades().get(upgrade);
        }
        return 0.0;
    }

    @Override
    public HashMap<String, Double> traits()
    {
        return this.traits;
    }

    @Override
    public double getTrait(String trait)
    {
        if (this.traits().containsKey(trait))
        {
            return this.traits().get(trait);
        }
        return 0.0;
    }

    @Override
    public int getCount()
    {
        return 0;
    }

    @Override
    public int getCount(String type)
    {
        return 0;
    }

    @Override
    public void onKill(Entity entity)
    {

    }
}
