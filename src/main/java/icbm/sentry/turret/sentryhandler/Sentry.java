package icbm.sentry.turret.sentryhandler;

import icbm.sentry.turret.TileSentry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.energy.IEnergyContainer;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.utility.nbt.ISaveObj;
import calclavia.lib.utility.nbt.SaveManager;

/**
 * Module way to deal with sentry guns
 * 
 * @author DarkGuardsman
 */
public class Sentry implements IEnergyContainer, ISaveObj
{
	protected Vector3 aimOffset;
	protected Vector3 centerOffset;
	public TileSentry host;
	protected float health;
	protected float maxHealth;
	protected EnergyStorageHandler energy;

	public Sentry(TileSentry host)
	{
		this.aimOffset = new Vector3(1, 0, 0);
		this.centerOffset = new Vector3(0.5, 0.5, 0.5);
		this.host = host;
		this.energy = new EnergyStorageHandler(1000);
		this.maxHealth = -1;
		this.health = 1000;
	}

	public void update()
	{

	}

	public boolean canFire()
	{
		return host.hasWorldObj();
	}

	public boolean fireWeapon(Vector3 target)
	{
		if (host.worldObj.isRemote)
			fireWeaponClient(target);
		
		
		return false;
	}

	/** visual rendering here */
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
		nbt.setString("id", SaveManager.getID(this.getClass()));
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

	public World world()
	{
		return this.host.worldObj;
	}

	public double x()
	{
		return this.host.xCoord;
	}

	public double y()
	{
		return this.host.yCoord;
	}

	public double z()
	{
		return this.host.zCoord;
	}
}
