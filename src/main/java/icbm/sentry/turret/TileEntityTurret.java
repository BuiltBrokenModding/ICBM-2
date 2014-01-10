package icbm.sentry.turret;

import icbm.Reference;
import icbm.core.ICBMCore;
import icbm.core.prefab.TileICBM;
import icbm.sentry.damage.EntityTileDamagable;
import icbm.sentry.damage.IHealthTile;
import icbm.sentry.interfaces.ISentry;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.task.LookHelper;
import icbm.sentry.task.RotationHelper;
import icbm.sentry.task.ServoMotor;

import java.text.MessageFormat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.network.PacketHandler;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

/**
 * Turret Base Class Class that handles all the basic movement, and block based updates of a turret.
 * 
 * @author Calclavia, Rseifert
 */
public abstract class TileEntityTurret extends TileICBM implements IPacketReceiver, ISentry, IHealthTile
{
	/** SPAWN DIRECTION OF SENTRY */
	private ForgeDirection platformDirection = ForgeDirection.DOWN;

	/** SHOULD SPEED UP ROATION */
	protected boolean speedUpRotation = false;
	/**
	 * Is the rotation helper enabled, true will cause the rotation helper to auto rotate the turret
	 */
	protected boolean enableRotationHelper = true;

	/** CURRENT HP OF THE SENTRY */
	public int health = -1;
	/** DEFUALT FIRING DELAY OF SENTRY */
	public int baseFiringDelay = 10;
	/** TICKS SINCE LAST GUN ACTIVATION */
	public int tickSinceFired = 0;
	/** LOWEST FIRING DELAY */
	public int minFiringDelay = 5;
	/** ENTITY THAT TAKES DAMAGE FOR THE SENTRY */
	private EntityTileDamagable damageEntity;

	/** Energy cost per tick to run this sentry */
	public long joulesPerTick = 0;

	/** Radius by which the weapon system can fire without clipping the sentries collision box */
	public float sentrySafeRadius = 1.5f;

	/** Weapon systems loaded into the turret */
	public IWeaponSystem[] weaponSystems;
	/** Helper class that deals with rotation */
	public RotationHelper rotationHelper;
	/** TURRET AIM & ROTATION HELPER */
	public LookHelper lookHelper = new LookHelper(this);
	/** Yaw servo rotation */
	public ServoMotor yawMotor;
	/** Pitch servo rotation */
	public ServoMotor pitchMotor;

	/** PACKET TYPES THIS TILE RECEIVES */
	public static enum TurretPacketType
	{
		ROTATION, DESCRIPTION, SHOT, STATS;
	}

	public TileEntityTurret()
	{
		this.rotationHelper = new RotationHelper(this);
		this.yawMotor = new ServoMotor(360, 0);
		this.pitchMotor = new ServoMotor(35, -35);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.tickSinceFired > 0)
		{
			this.tickSinceFired--;
		}

		if (!this.worldObj.isRemote)
		{
			// SPAWN DAMAGE ENTITY IF ALIVE
			if (!this.isInvul() && this.getDamageEntity() == null && this.getHealth() > 0)
			{
				this.setDamageEntity(new EntityTileDamagable(this));
				this.worldObj.spawnEntityInWorld(this.getDamageEntity());
			}
		}
		if (this.enableRotationHelper)
			this.rotationHelper.updateRotation(this.getRotationSpeed());
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

	/*
	 * ----------------------- PACKET DATA AND SAVING --------------------------------------
	 */
	@Override
	public boolean simplePacket(String id, ByteArrayDataInput dataStream, Player player)
	{
		try
		{
			if (id.equalsIgnoreCase(TurretPacketType.ROTATION.name()))
			{
				this.yawMotor.setRotation(dataStream.readFloat());
				this.pitchMotor.setRotation(dataStream.readFloat());
			}
			else if (id.equalsIgnoreCase(TurretPacketType.DESCRIPTION.name()))
			{
				this.readFromNBT(PacketHandler.instance().readNBTTagCompound(dataStream));
			}
			else if (id.equalsIgnoreCase(TurretPacketType.STATS.name()))
			{
				this.health = dataStream.readInt();
			}
		}
		catch (Exception e)
		{
			ICBMCore.LOGGER.severe(MessageFormat.format("Packet receiving failed: {0}", this.getClass().getSimpleName()));
			e.printStackTrace();
			return true;
		}
		return false;
	}

	public Packet getStatsPacket()
	{
		return PacketHandler.instance().getTilePacket(Reference.CHANNEL, TurretPacketType.STATS.name(), this, this.health);
	}

	public Packet getRotationPacket()
	{
		return PacketHandler.instance().getPacketFromLoader(Reference.CHANNEL, TurretPacketType.ROTATION.name(), this, this.rotationHelper);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return PacketHandler.instance().getPacket(Reference.CHANNEL, TurretPacketType.DESCRIPTION.name(), this, nbt);
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("dir", this.platformDirection.ordinal());
		nbt.setInteger("health", this.getHealth());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.platformDirection = ForgeDirection.getOrientation(nbt.getInteger("dir"));

		if (nbt.hasKey("health"))
		{
			this.health = nbt.getInteger("health");
		}
	}

	/*
	 * -------------------------- GENERIC SENTRY CODE --------------------------------------
	 */

	/** is this sentry currently operating */
	public boolean isRunning()
	{
		return this.getPlatform() != null && this.getPlatform().isFunctioning() && this.isAlive();
	}

	/** get the turrets control platform */
	@Override
	public TileEntityTurretPlatform getPlatform()
	{
		TileEntity tileEntity = this.worldObj.getBlockTileEntity(this.xCoord + this.platformDirection.offsetX, this.yCoord + this.platformDirection.offsetY, this.zCoord + this.platformDirection.offsetZ);

		if (tileEntity instanceof TileEntityTurretPlatform)
		{
			return (TileEntityTurretPlatform) tileEntity;
		}
		else
		{
			return null;
		}

	}

	/**
	 * Removes the sentry when called and optional creates a small local explosion
	 * 
	 * @param doExplosion - True too create a small local explosion
	 */
	public void destroy(boolean doExplosion)
	{
		if (doExplosion)
		{
			if (!this.isInvalid())
			{
				this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0);
				this.worldObj.createExplosion(this.getDamageEntity(), this.xCoord, this.yCoord, this.zCoord, 2f, true);
			}
			else
			{
				this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0);
			}
		}
		else if (!this.worldObj.isRemote)
		{
			this.getBlockType().dropBlockAsItem(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), 0);
			this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0);
		}
	}

	/** Gets the number of barrels this sentry has */
	public int getBarrels()
	{
		return 1;
	}

	/** Sets the deploy or connection direction of the sentry */
	public void setDeploySide(ForgeDirection side)
	{
		this.platformDirection = side.getOpposite();
	}

	@Override
	public VectorWorld getAimingDirection()
	{
		return new VectorWorld(this.worldObj, this.pos().add(Vector3.scale(new Vector3(this.getYawServo().getRotation(), this.getPitchServo().getRotation()), 1)));
	}

	public void onWeaponActivated()
	{
		this.tickSinceFired += this.getFireDelay();
	}

	public int getFireDelay()
	{
		return this.baseFiringDelay;
	}

	/*
	 * ------------------------------- HEALTH & DAMAGE CODE--------------------------------------
	 */
	/** Max Health this sentry has */
	@Override
	public abstract int getMaxHealth();

	/** Is the sentry immune to being attacked */
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public int getHealth()
	{
		if (this.health == -1)
		{
			this.health = this.getMaxHealth();
		}
		return this.health;
	}

	@Override
	public void setHealth(int i, boolean increase)
	{
		if (increase)
		{
			i += this.health;
		}

		this.health = Math.min(Math.max(i, 0), this.getMaxHealth());

		if (!this.worldObj.isRemote)
		{
			PacketHandler.instance().sendPacketToClients(this.getStatsPacket(), this.worldObj, new Vector3(this), 100);
		}
	}

	@Override
	public boolean isAlive()
	{
		return this.getHealth() > 0 || this.isInvul();
	}

	@Override
	public boolean onDamageTaken(DamageSource source, float amount)
	{
		if (this.isInvul())
		{
			return false;
		}
		else if (source != null && source.equals(DamageSource.onFire))
		{
			return true;
		}
		else
		{
			this.health -= amount;

			if (this.health <= 0)
			{
				this.destroy(true);
			}
			else
			{
				PacketHandler.instance().sendPacketToClients(this.getStatsPacket(), this.worldObj, new Vector3(this), 100);
			}

			return true;
		}
	}

	/** Entity that takes damage for the sentry */
	public EntityTileDamagable getDamageEntity()
	{
		return damageEntity;
	}

	/** Sets the entity that takes damage for this sentry */
	public void setDamageEntity(EntityTileDamagable damageEntity)
	{
		this.damageEntity = damageEntity;
	}

	public float getRotationSpeed()
	{
		return Float.MAX_VALUE;
	}

	@Override
	public IServo getYawServo()
	{
		return this.yawMotor;
	}

	@Override
	public IServo getPitchServo()
	{
		return this.pitchMotor;
	}

	public void rotateTo(float yaw, float pitch)
	{
		this.rotationHelper.setTargetRotation(yaw, pitch);
	}

	public void cancelRotation()
	{
		this.rotationHelper.setTargetRotation(this.getYawServo().getRotation(), this.getPitchServo().getRotation());
	}

	/*
	 * ----------------------------- CLIENT SIDE --------------------------------------
	 */
	public void drawParticleStreamTo(Vector3 endPosition)
	{
		if (this.worldObj.isRemote)
		{
			Vector3 startPosition = this.getAimingDirection();
			Vector3 direction = this.getAimingDirection();
			double xoffset = 0;
			double yoffset = 0;
			double zoffset = 0;
			Vector3 horzdir = direction.normalize();
			horzdir.y = 0;
			horzdir = horzdir.normalize();
			double cx = startPosition.x + direction.x * xoffset - direction.y * horzdir.x * yoffset - horzdir.z * zoffset;
			double cy = startPosition.y + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
			double cz = startPosition.z + direction.x * xoffset - direction.y * horzdir.x * yoffset + horzdir.x * zoffset;
			double dx = endPosition.x - cx;
			double dy = endPosition.y - cy;
			double dz = endPosition.z - cz;
			double ratio = Math.sqrt(dx * dx + dy * dy + dz * dz);

			while (Math.abs(cx - endPosition.x) > Math.abs(dx / ratio))
			{
				this.worldObj.spawnParticle("townaura", cx, cy, cz, 0.0D, 0.0D, 0.0D);
				cx += dx * 0.1 / ratio;
				cy += dy * 0.1 / ratio;
				cz += dz * 0.1 / ratio;
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getBoundingBox(this.xCoord - 1, this.yCoord - 1, this.zCoord - 1, this.xCoord + 2, this.yCoord + 2, this.zCoord + 2);
	}

	public long getJoulesPerTick()
	{
		return this.joulesPerTick;
	}

	public World getWorld()
	{
		return this.worldObj;
	}

	public Vector3 pos()
	{
		return Vector3.fromCenter(this);
	}

}
