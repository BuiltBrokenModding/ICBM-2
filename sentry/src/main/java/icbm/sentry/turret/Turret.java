package icbm.sentry.turret;

import icbm.sentry.interfaces.IEnergyTurret;
import icbm.sentry.interfaces.IEnergyWeapon;
import icbm.sentry.interfaces.IKillCount;
import icbm.sentry.interfaces.ISentryTrait;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.interfaces.IUpgrade;
import icbm.sentry.interfaces.IWeaponProvider;
import icbm.sentry.interfaces.IWeaponSystem;
import icbm.sentry.turret.ai.EulerServo;
import icbm.sentry.turret.ai.TurretAI;
import icbm.sentry.turret.traits.SentryTrait;
import icbm.sentry.turret.traits.SentryTraitDouble;
import icbm.sentry.turret.traits.SentryTraitInteger;
import icbm.sentry.turret.traits.SentryTraitLong;
import icbm.sentry.turret.weapon.WeaponSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;
import calclavia.lib.utility.nbt.SaveManager;

/** Modular way to deal with sentry guns
 * 
 * @author DarkGuardsman, tgame14 */
public abstract class Turret implements IEnergyTurret, IWeaponProvider, IKillCount
{

    /** Host of the sentry gun */
    private final ITurretProvider host;
    /** Count of upgrades */
    private final HashMap<String, Double> upgrade_count = new HashMap<String, Double>();
    /** Traits applied to the sentry */
    private final HashMap<String, ISentryTrait> traits = new HashMap<String, ISentryTrait>();
    /** Count of kills */
    private final HashMap<String, Integer> kill_count = new HashMap<String, Integer>();

    /** Turret Weapon system */
    protected WeaponSystem weaponSystem;

    /** Offset from the center point were the barrel would be at zero rotation */
    protected Vector3 barrelOffset = new Vector3();
    /** Offset from the host location were the center of the sentry's turret is located */
    protected Vector3 centerOffset = new Vector3();

    private int cooldown = 0;
    protected long ticks = 0;
    protected float barrelLength = 1;

    /** Axis and rotation controller */
    private EulerServo servo;
    /** Energy storage unit for the turret */
    public EnergyStorageHandler battery;
    /** AI for the sentry, defines how the sentry acts without a controller */
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
                return SentryTrait.asLong(Turret.this.getTrait(ITurret.ENERGY_STORAGE_TRAIT), this.capacity);
            }

            @Override
            public long getMaxReceive()
            {
                return this.getEnergyCapacity();
            }

            @Override
            public long getMaxExtract()
            {
                return this.getEnergyCapacity();
            }
        };

        newTrait(new SentryTraitLong(ITurret.ENERGY_STORAGE_TRAIT, IUpgrade.ENERGY_STORAGE, 100000));
        newTrait(new SentryTraitLong(ITurret.ENERGY_RUNNING_TRAIT, IUpgrade.ENERGY_EFFICIENCY, 10));
        newTrait(new SentryTraitDouble(ITurret.MAX_HEALTH_TRAIT, 50.0));
        newTrait(new SentryTraitDouble(ITurret.SEARCH_RANGE_TRAIT, IUpgrade.TARGET_RANGE, 20.0));
        newTrait(new SentryTraitDouble(ITurret.ROTATION_SPEED_TRAIT, 5.0));
        newTrait(new SentryTraitDouble(ITurret.ROTATION_SPEED_WITH_TARGET_TRAIT, 7.0));
        newTrait(new SentryTraitInteger(ITurret.AMMO_RELOAD_TIME_TRAIT, 20));
    }

    /** Called directly after the sentry has loaded into the world & updated once */
    public void init()
    {
        this.onInventoryChanged();
        this.onSettingsChanged();
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
    public void newTrait(ISentryTrait<?> value)
    {
        if (this.ticks == 0)
        {
            this.traits.put(value.getName(), value);
        }
    }

    /** Sets the default value of a trait */
    public void setTrait(String value, Object data)
    {
        //System.out.println("Value: " + value + "  Data: " + data);
        if (this.ticks == 0)
        {
            if (data != null)
            {
                ISentryTrait<Object> trait = getTrait(value);
                //System.out.println("Before>Trait: " + trait.toString());
                if (trait != null)
                {
                    try
                    {
                        trait.setDefaultValue(data);
                        trait.setValue(data);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                //System.out.println("After>Trait: " + trait.toString());
            }
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
            cooldown = SentryTrait.asInt(getTrait(ITurret.AMMO_RELOAD_TIME_TRAIT), 13);
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
            cooldown = SentryTrait.asInt(getTrait(ITurret.AMMO_RELOAD_TIME_TRAIT), 10);
            return true;
        }
        return false;
    }

    @Override
    public Vector3 getWeaponOffset()
    {
        return new Vector3(getServo()).scale(barrelLength).translate(barrelOffset);
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

        //Save kill counts
        NBTTagList killCounts = new NBTTagList();
        for (Entry<String, Integer> entry : this.kill_count.entrySet())
        {
            if (entry.getKey() != null && !entry.getKey().isEmpty())
            {
                NBTTagCompound accessData = new NBTTagCompound();
                accessData.setString("name", entry.getKey());
                accessData.setInteger("count", entry.getValue());
                killCounts.appendTag(accessData);
            }
        }
        nbt.setTag("killCount", killCounts);
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

        //Load kill count
        NBTTagList nodeList = nbt.getTagList("killCount");
        kill_count.clear();
        for (int i = 0; i < nodeList.tagCount(); ++i)
        {
            NBTTagCompound tag = (NBTTagCompound) nodeList.tagAt(i);
            kill_count.put(tag.getString("name"), tag.getInteger("count"));
        }

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

        //Update upgrade count
        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            if (inv.getStackInSlot(slot) != null && inv.getStackInSlot(slot).getItem() instanceof IUpgrade && ((IUpgrade) inv.getStackInSlot(slot).getItem()).canApplyTo(inv.getStackInSlot(slot), this))
            {
                final List<String> id_list = new ArrayList<String>();
                ((IUpgrade) inv.getStackInSlot(slot).getItem()).getTypes(id_list, inv.getStackInSlot(slot));
                for (String id : id_list)
                {
                    if (!this.upgrade_count.containsKey(id))
                    {
                        this.upgrade_count.put(id, 0.0D);
                    }
                    this.upgrade_count.put(id, this.upgrade_count.get(id) + ((IUpgrade) inv.getStackInSlot(slot).getItem()).getUpgradeEfficiance(inv.getStackInSlot(slot), id));
                }
            }
        }

        //Update traits
        for (Entry<String, ISentryTrait> entry : this.traits().entrySet())
        {
            if (entry.getValue() != null)
            {
                entry.getValue().updateTrait(this);
            }
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
    public HashMap<String, ISentryTrait> traits()
    {
        return this.traits;
    }

    @Override
    public ISentryTrait getTrait(String trait)
    {
        if (this.traits().containsKey(trait))
        {
            return this.traits().get(trait);
        }
        return new SentryTraitDouble(trait, 0.0);
    }

    @Override
    public int getKillCount()
    {
        return getKillCount("Total");
    }

    @Override
    public int getKillCount(String type)
    {
        return kill_count.containsKey(type) ? kill_count.get(type) : 0;
    }

    @Override
    public void onKillOfEntity(Entity entity)
    {
        if (entity != null)
        {
            if (entity instanceof IMob)
            {
                increaseKill("Mobs");
            }
            else if (entity instanceof INpc)
            {
                increaseKill("NPCs");
            }
            else if (entity instanceof IAnimals)
            {
                increaseKill("Animals");
            }
            else if (entity instanceof EntityPlayer)
            {
                increaseKill("Players");
                increaseKill(((EntityPlayer)entity).username);
            }
            else
            {
                increaseKill(EntityList.getEntityString(entity));
            }
            increaseKill("Total");
        }
    }

    /** Increase the kill count */
    private void increaseKill(String type)
    {
        if (type != null && !type.isEmpty())
            kill_count.put(type, 1 + (kill_count.containsKey(type) ? kill_count.get(type) : 0));
    }

    @Override
    public long getRunningCost()
    {
        return SentryTrait.asLong(getTrait(ITurret.ENERGY_RUNNING_TRAIT), 0L);
    }
}
