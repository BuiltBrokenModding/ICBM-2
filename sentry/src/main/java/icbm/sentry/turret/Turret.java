package icbm.sentry.turret;

import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.turret.weapon.WeaponSystem;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.utility.nbt.SaveManager;

/**
 * Modular way to deal with sentry guns
 * 
 * @author DarkGuardsman, tgame14
 */
public abstract class Turret implements IEnergyContainer, ITurret
{
	// TODO: implement a property system used by MC entities to support any number of settings a
	// sentry can have
	protected float maxHealth = -1;
	public ITurretProvider host;
	protected Vector3 aimOffset;
	protected float barrelLength;
	protected Vector3 centerOffset;
	protected float health;
	protected EnergyStorageHandler energy;
	protected int range = 10;
	// TODO: change out weapon system var for an interface and registry system
	protected WeaponSystem weaponSystem;

	public Turret(ITurretProvider host)
	{
		this.host = host;
		aimOffset = new Vector3();
		centerOffset = new Vector3();
		barrelLength = 1;
		energy = new EnergyStorageHandler(1000);
	}

	public float getMaxHealth()
	{
		return maxHealth;
	}

	public void update()
	{

	}

	public boolean canFire()
	{
		// TODO do ammo check
		return true;
	}

	@Override
	public boolean fire(Vector3 target)
	{
		if (canFire())
		{
			if (getHost().world().isRemote)
			{
				weaponSystem.renderClient(target);
			}
			else
			{
				getHost().sendFireEventToClient(target);
				weaponSystem.fire(target);
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean fire(Entity target)
	{
		if (canFire())
		{
			if (getHost().world().isRemote)
			{
				weaponSystem.renderClient(Vector3.fromCenter(target));
			}
			else
			{
				getHost().sendFireEventToClient(Vector3.fromCenter(target));
				weaponSystem.fire(target);
			}

			return true;
		}
		return false;
	}

	/**
	 * Offset from the center offset to were the end of the barrel should be at. This is RELATIVE to
	 * the center!
	 */
	public Vector3 getAimOffset()
	{
		return new Vector3(getHost().yaw(), getHost().pitch()).scale(barrelLength);
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
		nbt.setString(ITurret.SENTRY_SAVE_ID, SaveManager.getID(this.getClass()));
		if (this.energy != null)
			this.energy.writeToNBT(nbt);

	}

	@Override
	public void load(NBTTagCompound nbt)
	{
		if (this.energy != null)
			this.energy.readFromNBT(nbt);

	}

	@Override
	public ITurretProvider getHost()
	{
		return this.host;
	}

	protected World world()
	{
		return this.getHost().world();
	}

	protected double x()
	{
		return this.getHost().x();
	}

	protected double y()
	{
		return this.getHost().y();
	}

	protected double z()
	{
		return this.getHost().z();
	}

	protected Vector3 getPosition()
	{
		return new Vector3(x(), y(), z());
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
		return "[Sentry]ID: " + (id != null ? id : "unknown") + "   " + super.toString();
	}

}
