package icbm.gangshao.turret;

import icbm.api.sentry.ISentry;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.actions.ActionManager;
import icbm.gangshao.actions.LookHelper;
import icbm.gangshao.platform.TileEntityTurretPlatform;

import java.io.IOException;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IVoltage;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import calclavia.lib.render.ITagRender;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import dark.core.api.IHeatObject;
import dark.library.damage.EntityTileDamage;
import dark.library.damage.IHpTile;

/** Class that handles all the basic movement, and block based updates of a turret.
 * 
 * @author Rseifert */
public abstract class TileEntityTurretBase extends TileEntityAdvanced implements IPacketReceiver, ITagRender, IVoltage, ISentry, IHpTile, IHeatObject
{
	/** OFFSET OF BARREL ROTATION */
	public final float rotationTranslation = 0.0175f;
	/** MAX UPWARD PITCH ANGLE OF THE SENTRY BARREL */
	public static final float MAX_PITCH = 30f;
	/** MAX DOWNWARD PITCH ANGLE OF THE SENTRY BARREL */
	public static final float MIN_PITCH = -30f;
	/** SPAWN DIRECTION OF SENTRY */
	private ForgeDirection platformDirection = ForgeDirection.DOWN;
	/** AI HELPERS */
	public final ActionManager actionManager = new ActionManager();
	/** TURRET AIM & ROTATION HELPER */
	public LookHelper lookHelper = new LookHelper(this);
	/** SHOULD SPEED UP ROATION */
	protected boolean speedUpRotation = false;
	/** CURRENT HP OF THE SENTRY */
	public int health = -1;
	/** CURRENT STORED HEAT LEVEL OF SENTRY */
	public double heat = 0;
	/** BASE MAX LIMIT OF HEAT THIS SENTRY CAN HANDLE */
	public double maxHeat = 500;
	/** DEFUALT FIRING DELAY OF SENTRY */
	public int baseFiringDelay = 10;
	/** TICKS SINCE LAST GUN ACTIVATION */
	public int tickSinceFired = 0;
	/** LOWEST FIRING DELAY */
	public int minFiringDelay = 5;
	/** ENTITY THAT TAKES DAMAGE FOR THE SENTRY */
	private EntityTileDamage damageEntity;
	/** TARGET ROATION ANGLES */
	public float wantedRotationYaw, wantedRotationPitch = 0;
	/** CURRENT ROATION ANGLES */
	public float currentRotationYaw, currentRotationPitch = 0;
	/** CURRENT BARREL IN USE... USED TO SIMULATE MULTI BARREL SENTRIES */
	protected int gunBarrel = 0;

	/** PACKET TYPES THIS TILE RECEIVES */
	public static enum turretPacket
	{
		ROTATION(),
		NBT(),
		SHOT(),
		STATS(),
		MOUNT();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		float prePitch = this.wantedRotationPitch;
		float preYaw = this.wantedRotationYaw;

		if (tickSinceFired > 0)
		{
			--tickSinceFired;
		}

		if (this.isRunning())
		{
			this.updateRotation();
			this.onUpdate();			
		}

		if (!this.worldObj.isRemote)
		{
			int preHp = this.hp();
			double preHeat = this.heat;

			/* CREATE HEAT DAMAGE IF HEAT LEVEL IS TOO HIGH */
			if ((this.heat -= this.getCoolingRate(ForgeDirection.UNKNOWN)) > this.getSafeHeatLvL() && this.ticks % 5 == 0)
			{
				this.health -= 1;
			}

			/* SPAWN DAMAGE ENTITY IF ALIVE AND ABLE */
			if (!this.isInvul() && this.getDamageEntity() == null && this.hp() > 0)
			{
				this.setDamageEntity(new EntityTileDamage(this));
				this.worldObj.spawnEntityInWorld(this.getDamageEntity());
			}

			/* ROTATION PACKET */
			if (this.wantedRotationPitch != prePitch || this.wantedRotationYaw != preYaw)
			{
				PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.ROTATION.ordinal(), this.wantedRotationPitch, this.wantedRotationYaw, this.speedUpRotation), this.worldObj, new Vector3(this), 50);
			}

			/* STATS PACKET */
			if (this.getHeat(ForgeDirection.UNKNOWN) != preHeat || this.hp() != preHp)
			{
				PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.STATS.ordinal(), this.heat, this.hp()), this.worldObj, new Vector3(this), 50);
			}
		}

	}

	/** Called by updateEntity after checks are made to see if turret can function */
	public abstract void onUpdate();

	/** Energy consumed each time the weapon activates */
	public abstract double getFiringRequest();

	/** Energy consumed per tick to keep the sentry running */
	public abstract double getRunningRequest();

	/** is this sentry currently operating */
	public boolean isRunning()
	{
		return this.getPlatform() != null && this.getPlatform().isRunning() && this.isAlive();
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

	/*
	 * ----------------------- PACKET DATA AND SAVING --------------------------------------
	 */
	/** Data */
	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (this.worldObj.isRemote)
		{
			try
			{
				int id = dataStream.readInt();

				if (id == turretPacket.ROTATION.ordinal())
				{
					this.wantedRotationPitch = dataStream.readFloat();
					this.wantedRotationYaw = dataStream.readFloat();
					this.speedUpRotation = dataStream.readBoolean();
				}
				else if (id == turretPacket.NBT.ordinal())
				{
					short size = dataStream.readShort();

					if (size > 0)
					{
						byte[] byteCode = new byte[size];
						dataStream.readFully(byteCode);
						this.readFromNBT(CompressedStreamTools.decompress(byteCode));
					}
				}
				else if (id == turretPacket.SHOT.ordinal())
				{
					this.gunBarrel = dataStream.readInt();
					this.renderShot(new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble()));
					this.playFiringSound();
				}
				else if (id == turretPacket.STATS.ordinal())
				{
					this.setHeat(dataStream.readDouble(), false);
					this.health = dataStream.readInt();
				}
				else if (id == turretPacket.MOUNT.ordinal())
				{
					this.mount(player);
				}
			}
			catch (IOException e)
			{
				FMLLog.severe("Failed to receive packet for Sentry");
				e.printStackTrace();
			}
		}
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setFloat("yaw", this.wantedRotationYaw);
		nbt.setFloat("pitch", this.wantedRotationPitch);
		nbt.setFloat("cYaw", this.currentRotationYaw);
		nbt.setFloat("cPitch", this.currentRotationPitch);
		nbt.setInteger("dir", this.platformDirection.ordinal());
		nbt.setInteger("health", this.hp());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.wantedRotationYaw = nbt.getFloat("yaw");
		this.wantedRotationPitch = nbt.getFloat("pitch");
		this.currentRotationYaw = nbt.getFloat("cYaw");
		this.currentRotationPitch = nbt.getFloat("cPitch");
		this.platformDirection = ForgeDirection.getOrientation(nbt.getInteger("dir"));
		if (nbt.hasKey("health"))
		{
			this.health = nbt.getInteger("health");
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.NBT.ordinal(), nbt);
	}

	/** Sends the firing info to the client to render tracer effects */
	public void sendShotToClient(Vector3 target)
	{
		if(target == null)
		{
			target = new Vector3();
		}
		this.gunBarrel++;
		PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.SHOT.ordinal(), target.x, target.y, target.z, this.gunBarrel), this.worldObj, new Vector3(this), 50);

		if (this.gunBarrel >= (this.getBarrels() - 1))
		{
			this.gunBarrel = 0;
		}
	}

	/** creates client side effects when the sentry fires its weapon */
	public abstract void renderShot(Vector3 target);

	/** Sound this turrets plays each time its fires */
	public abstract void playFiringSound();

	/*
	 * -------------------------- GENERIC SENTRY CODE --------------------------------------
	 */

	/** Removes the sentry when called and optional creates a small local explosion
	 * 
	 * @param doExplosion - True too create a small local explosion */
	public boolean destroy(boolean doExplosion)
	{
		if (doExplosion)
		{
			this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 2f, true);
		}

		if (!this.worldObj.isRemote)
		{
			this.getBlockType().dropBlockAsItem(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), 0);
		}

		return this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0);
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
	public float addInformation(HashMap<String, Integer> map, EntityPlayer player)
	{
		map.put(this.getName(), 0x88FF88);
		return 1;
	}

	@Override
	public String getName()
	{
		return new ItemStack(this.getBlockType(), 1, this.getBlockMetadata()).getDisplayName() + " " + this.hp() + "/" + this.getMaxHealth();
	}

	/** Performs a ray trace for the distance specified and using the partial tick time. Args:
	 * distance, partialTickTime */
	public MovingObjectPosition rayTrace(double distance)
	{
		Vector3 muzzlePosition = this.getMuzzle();
		Vector3 lookDistance = LookHelper.getDeltaPositionFromRotation(this.wantedRotationYaw / this.rotationTranslation, this.wantedRotationPitch / this.rotationTranslation);
		Vector3 var6 = Vector3.add(muzzlePosition, Vector3.multiply(lookDistance, distance));
		return this.worldObj.rayTraceBlocks(muzzlePosition.toVec3(), var6.toVec3());
	}

	@Override
	public Vector3 getMuzzle()
	{
		Vector3 position = new Vector3(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
		return Vector3.add(position, Vector3.multiply(LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch - 10), 0.5));
	}

	@Override
	public void onWeaponActivated()
	{
		this.tickSinceFired += this.baseFiringDelay;
		this.heat += this.getHeatPerShot();
	}

	public void mount(EntityPlayer entityPlayer)
	{

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
	public int hp()
	{
		if (this.health == -1)
		{
			this.health = this.getMaxHealth();
		}
		return this.health;
	}

	@Override
	public void setHp(int i, boolean increase)
	{
		if (increase)
		{
			i += this.health;
		}
		this.health = Math.min(Math.max(i, 0), this.getMaxHealth());
	}

	@Override
	public boolean isAlive()
	{
		return this.hp() > 0 || this.isInvul();
	}

	@Override
	public boolean onDamageTaken(DamageSource source, int amount)
	{
		if (this.isInvul())
		{
			return false;
		}
		else if (source != null && source.equals(DamageSource.onFire))
		{
			this.heat += amount * 2;
			return true;
		}
		else
		{
			this.health -= amount;
			if (!this.worldObj.isRemote)
			{
				PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.STATS.ordinal(), this.heat, this.hp()), this.worldObj, new Vector3(this), 50);
			}
			return true;
		}
	}

	/** Entity that takes damage for the sentry */
	public EntityTileDamage getDamageEntity()
	{
		return damageEntity;
	}

	/** Sets the entity that takes damage for this sentry */
	public void setDamageEntity(EntityTileDamage damageEntity)
	{
		this.damageEntity = damageEntity;
	}

	/*
	 * ------------------------------------- HEAT CODE--------------------------------------
	 */
	/** Heat generated each time the turret fires */
	public abstract double getHeatPerShot();

	/** Max level this sentry can take in heat before taking damage */
	public double getSafeHeatLvL()
	{
		return this.maxHeat + Math.max(this.maxHeat * this.getPlatform().getUpgradePercent("HeatSink"), 10000);
	}

	@Override
	public double getHeat(ForgeDirection side)
	{
		return this.heat;
	}

	@Override
	public void setHeat(double amount, boolean increase)
	{
		if (increase)
		{
			amount += this.health;
		}
		this.heat = Math.min(Math.max(amount, 0), 100000);
	}

	@Override
	public double getCoolingRate(ForgeDirection side)
	{
		return 20;
	}

	/*
	 * ----------------------------- ROTATION CODE --------------------------------------
	 */
	@Override
	public void setRotation(float yaw, float pitch)
	{
		this.wantedRotationYaw = yaw;
		this.wantedRotationPitch = pitch;
	}

	/** Speed of rotation at the current moment */
	public abstract float getRotationSpeed();

	/** Adjusts the turret's rotation to its target rotation over time. */
	public void updateRotation()
	{
		if (Math.abs(this.currentRotationYaw - this.wantedRotationYaw) > 0.001f)
		{
			float speedYaw;
			if (this.currentRotationYaw > this.wantedRotationYaw)
			{
				speedYaw = -this.getRotationSpeed();
			}
			else
			{
				speedYaw = this.getRotationSpeed();
			}

			this.currentRotationYaw += speedYaw;

			if (Math.abs(this.currentRotationYaw - this.wantedRotationYaw) < this.getRotationSpeed() + 0.1f)
			{
				this.currentRotationYaw = this.wantedRotationYaw;
			}
		}

		if (Math.abs(this.currentRotationPitch - this.wantedRotationPitch) > 0.001f)
		{
			float speedPitch;
			if (this.currentRotationPitch > this.wantedRotationPitch)
			{
				speedPitch = -this.getRotationSpeed();
			}
			else
			{
				speedPitch = this.getRotationSpeed();
			}

			this.currentRotationPitch += speedPitch;

			if (Math.abs(this.currentRotationPitch - this.wantedRotationPitch) < this.getRotationSpeed() + 0.1f)
			{
				this.currentRotationPitch = this.wantedRotationPitch;
			}
		}

		/** Wraps all the angels and cleans them up. */
		this.currentRotationPitch = MathHelper.wrapAngleTo180_float(this.currentRotationPitch);
		this.wantedRotationYaw = MathHelper.wrapAngleTo180_float(this.wantedRotationYaw);
		this.wantedRotationPitch = MathHelper.wrapAngleTo180_float(this.wantedRotationPitch);
	}
	
	
	
	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

}
