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
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IVoltage;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import calclavia.lib.render.ITagRender;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import dark.hydraulic.api.IHeatObject;
import dark.library.damage.EntityTileDamage;
import dark.library.damage.IHpTile;

/**
 * Class that handles all the basic movement, and block based updates of a turret.
 * 
 * @author Rseifert
 * 
 */
public abstract class TileEntityTurretBase extends TileEntityAdvanced implements IPacketReceiver, ITagRender, IVoltage, ISentry, IHpTile, IHeatObject
{
	public static final float MAX_PITCH = 30f;

	public static final float MIN_PITCH = -30f;

	private ForgeDirection platformDirection = ForgeDirection.DOWN;

	public final ActionManager actionManager = new ActionManager();
	public LookHelper lookHelper = new LookHelper(this);

	protected boolean speedUpRotation = false;

	public int hp = this.getMaxHealth();
	public double heat = 0;

	public EntityTileDamage damageEntity;
	/**
	 * The rotation of the arms. In Degrees.
	 */
	public float wantedRotationYaw, wantedRotationPitch = 0;
	public float currentRotationYaw, currentRotationPitch = 0;

	protected int gunBarrel = 0;

	public enum turretPacket
	{
		ROTATION(), NBT(), SHOT(), STATS();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		float prePitch = this.wantedRotationPitch;
		float preYaw = this.wantedRotationYaw;

		int preHp = this.hp;
		double preHeat = this.heat;

		if (this.isRunning())
		{
			this.onUpdate();
			this.updateRotation();
		}

		if (!this.worldObj.isRemote)
		{
			if ((this.heat -= this.getCoolingRate(ForgeDirection.UNKNOWN)) > this.getSafeHeatLvL() && this.ticks % 5 == 0)
			{
				this.hp -= 1;
			}
			if (!this.isInvul() && this.damageEntity == null && this.hp > 0)
			{
				this.damageEntity = new EntityTileDamage(this);
			}
			if (this.wantedRotationPitch != prePitch || this.wantedRotationYaw != preYaw)
			{
				PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.ROTATION.ordinal(), this.wantedRotationPitch, this.wantedRotationYaw, this.speedUpRotation), this.worldObj, new Vector3(this), 50);
			}
			if (this.getHeat(ForgeDirection.UNKNOWN) != preHeat || this.hp != preHp)
			{
				PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.STATS.ordinal(), this.heat, this.hp), this.worldObj, new Vector3(this), 50);
			}
		}

	}

	/**
	 * Called by updateEntity after checks are made to see if turret can function
	 */
	protected abstract void onUpdate();

	/**
	 * get the turrets control platform
	 */
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
	 * Adjusts the turret's rotation to its target rotation over time.
	 */
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

		/**
		 * Wraps all the angels and cleans them up.
		 */
		this.currentRotationPitch = MathHelper.wrapAngleTo180_float(this.currentRotationPitch);
		this.wantedRotationYaw = MathHelper.wrapAngleTo180_float(this.wantedRotationYaw);
		this.wantedRotationPitch = MathHelper.wrapAngleTo180_float(this.wantedRotationPitch);
	}

	/**
	 * is this sentry currently operating
	 */
	public boolean isRunning()
	{
		return this.getPlatform() != null && this.getPlatform().isRunning() && this.hp > 0;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.NBT.ordinal(), nbt);
	}

	/**
	 * Sends the firing info to the client to render tracer effects
	 */
	public void sendShotToClient(Vector3 target)
	{
		this.gunBarrel++;
		PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, turretPacket.SHOT.ordinal(), target.x, target.y, target.z, this.gunBarrel), this.worldObj, new Vector3(this), 50);

		if (this.gunBarrel >= (this.getBarrels() - 1))
		{
			this.gunBarrel = 0;
		}
	}

	/**
	 * Data
	 */
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
					ZhuYaoGangShao.proxy.renderTracer(this.worldObj, this.getMuzzle(), new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble()));
					this.gunBarrel = dataStream.readInt();
				}
				else if (id == turretPacket.STATS.ordinal())
				{
					this.setHeat(dataStream.readDouble(), false);
					this.hp = dataStream.readInt();
				}
			}
			catch (IOException e)
			{
				FMLLog.severe("Failed to receive packet for Sentry");
				e.printStackTrace();
			}
		}
	}

	public int getBarrels()
	{
		return 1;
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setFloat("yaw", this.wantedRotationYaw);
		nbt.setFloat("pitch", this.wantedRotationPitch);
		nbt.setInteger("dir", this.platformDirection.ordinal());
		nbt.setInteger("health", this.hp);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.wantedRotationYaw = nbt.getFloat("yaw");
		this.wantedRotationPitch = nbt.getFloat("pitch");
		this.platformDirection = ForgeDirection.getOrientation(nbt.getInteger("dir"));
		if (nbt.hasKey("health"))
		{
			this.hp = nbt.getInteger("health");
		}
	}

	/**
	 * removes the turret from existence optional create an explosion in the process
	 * 
	 * @param doExplosion - True if the turret is to explode.
	 */
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

	/**
	 * sets the direction by which the turret will deploy too Causes the model to rotate to match
	 * the direction
	 * 
	 * @param side
	 */
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
		return new ItemStack(this.getBlockType(), 1, this.getBlockMetadata()).getDisplayName();
	}

	@Override
	public void setRotation(float yaw, float pitch)
	{
		this.wantedRotationYaw = yaw;
		this.wantedRotationPitch = pitch;
	}

	@Override
	public Vector3 getMuzzle()
	{
		Vector3 position = new Vector3(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
		return Vector3.add(position, Vector3.multiply(LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch - 10), 0.5));
	}

	@Override
	public boolean onDamageTaken(DamageSource source, int ammount)
	{
		if (this.isInvul())
		{
			return false;
		}
		else if (source != null && source.equals(DamageSource.onFire))
		{
			this.heat += 10;
			return false;
		}
		else
		{
			this.hp -= ammount;
			return false;
		}
	}

	/**
	 * Is this tile immune to attacks
	 */
	public boolean isInvul()
	{
		return false;
	}

	public abstract float getRotationSpeed();

	/**
	 * Max Health this tile has to be damaged
	 */
	public abstract int getMaxHealth();

	/**
	 * Max point before the turret starts taking heat damage
	 */
	public abstract double getSafeHeatLvL();

	@Override
	public boolean isAlive()
	{
		return this.hp > 0;
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
			this.heat += amount;
		}
		else
		{
			this.heat = amount;
		}
	}

	@Override
	public double getCoolingRate(ForgeDirection side)
	{
		return 20;
	}
}
