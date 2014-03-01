package icbm.sentry.turret.block;

import calclavia.lib.access.AccessProfile;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.AutoServo;
import calclavia.lib.prefab.IGyroMotor;
import calclavia.lib.prefab.tile.IRotatable;
import calclavia.lib.terminal.TileTerminal;
import calclavia.lib.utility.inventory.ExternalInventory;
import calclavia.lib.utility.inventory.IExternalInventory;
import calclavia.lib.utility.inventory.IExternalInventoryBox;

import com.google.common.io.ByteArrayDataInput;

import icbm.core.ICBMCore;
import icbm.sentry.ICBMSentry;
import icbm.sentry.interfaces.ISentry;
import icbm.sentry.interfaces.ISentryContainer;
import icbm.sentry.turret.EntitySentryFake;
import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.SentryRegistry;
import icbm.sentry.turret.ai.LookHelper;
import icbm.sentry.turret.ai.SentryAI;
import icbm.sentry.turret.mount.MountedSentry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;

/**
 * Tile container for sentries
 * 
 * @author Darkguardsman, tgame14
 */
public class TileTurret extends TileTerminal implements IProfileContainer, IRotatable, IGyroMotor, IExternalInventory, IBlockActivate, ISentryContainer
{
	protected static final int ROTATION_PACKET_ID = 3;
	protected static final int SENTRY_TYPE_PACKET_ID = 4;
	protected static final int DESCRIPTION_PACKET_ID = 5;
	protected static final int FIRING_EVENT_PACKET_ID = 6;
	/** TURRET AIM & ROTATION HELPER */
	public LookHelper lookHelper;
	/** Yaw servo rotation */
	public AutoServo yawMotor;
	/** Pitch servo rotation */
	public AutoServo pitchMotor;
	public EntitySentryFake sentryEntity;
	/** Profile that control access properties for users */
	protected AccessProfile accessProfile;
	/** Sentries inventory used for upgrades and ammo */
	protected IExternalInventoryBox inventory;
	/** Sentry instance used to define the visuals and weapons of the sentry */
	protected Sentry sentry;

	protected SentryAI sentryAI;

	private static float[] yawData = { 180, -180, 5F };
	private static float[] pitchData = { 40, -40, 5F };
	private String unlocalizedName = "err";
	private String saveManagerSentryKey;

	public TileTurret()
	{
		super();
		this.inventory = new ExternalInventory(this, 8);
		this.energy = new EnergyStorageHandler(1000);
	}

	@Override
	public void initiate()
	{
		super.initiate();
		this.yawMotor = new AutoServo(yawData[0], yawData[1], yawData[2]);
		this.pitchMotor = new AutoServo(pitchData[0], pitchData[1], pitchData[2]);
		this.lookHelper = new LookHelper(this);
		this.sentryAI = new SentryAI(this, this.lookHelper);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		float prevYaw = this.getYawServo().getRotation();
		float prevPitch = this.getPitchServo().getRotation();

		if (this.getSentry() != null)
		{
			getSentry().updateEntity();
			sentryAI.update();
		}

		this.getYawServo().update();
		this.getPitchServo().update();

		// TODO check to make sure rotation has changed more then 1 degree so avoid sending too many
		// packets
		if (prevYaw != this.getYawServo().getRotation() || prevPitch != this.getPitchServo().getRotation())
		{
			PacketHandler.sendPacketToClients(this.getRotationPacket(), this.getWorldObj(), new Vector3(this), 60);
		}
	}

	/** will be moved to SentryAI */
	@Deprecated
	protected void mountableSentryLoop()
	{
		boolean flag = false;
		if (this.hasWorldObj() && (this.sentryEntity == null || this.sentryEntity.isDead))
		{
			this.sentryEntity = new EntitySentryFake(this, true);
			this.worldObj.spawnEntityInWorld(this.sentryEntity);
			flag = true;
		}

		// TODO set up handling for non-player entities, low Priority
		if (flag && this.sentryEntity.riddenByEntity instanceof EntityPlayer)
		{
			EntityPlayer mountedPlayer = (EntityPlayer) this.sentryEntity.riddenByEntity;

			if (mountedPlayer.rotationPitch > this.getPitchServo().upperLimit())
			{
				mountedPlayer.rotationPitch = this.getPitchServo().upperLimit();
			}
			if (mountedPlayer.rotationPitch < this.getPitchServo().lowerLimit())
			{
				mountedPlayer.rotationPitch = this.getPitchServo().lowerLimit();
			}
			this.getPitchServo().setRotation(mountedPlayer.rotationPitch);
			this.getYawServo().setRotation(mountedPlayer.rotationYaw);
		}

	}

	@Override
	public AccessProfile getAccessProfile()
	{
		if (this.accessProfile == null)
		{
			this.setAccessProfile(new AccessProfile().generateNew("default", this));
		}
		return accessProfile;
	}

	@Override
	public void setAccessProfile(AccessProfile profile)
	{
		this.accessProfile = profile;
	}

	@Override
	public boolean canAccess(String username)
	{
		return this.getAccessProfile().getUserAccess(username) != null;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return ICBMCore.PACKET_TILE.getPacketWithID(DESCRIPTION_PACKET_ID, this, saveManagerSentryKey, this.yaw(), this.pitch());
	}

	public Packet getNBTPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return ICBMCore.PACKET_TILE.getPacketWithID(NBT_PACKET_ID, this, tag);
	}

	@Override
	public Packet getTerminalPacket()
	{
		return ICBMCore.PACKET_TILE.getPacketWithID(TERMINAL_PACKET_ID, this, this.getPacketData(1).toArray());
	}

	@Override
	public Packet getCommandPacket(String username, String cmdInput)
	{
		return ICBMCore.PACKET_TILE.getPacketWithID(COMMAND_PACKET_ID, this, username, cmdInput);
	}

	public Packet getRotationPacket()
	{
		return ICBMCore.PACKET_TILE.getPacketWithID(ROTATION_PACKET_ID, this, this.getYawServo().getRotation(), this.getPitchServo().getRotation());
	}

	@Override
	public void sendFireEventToClient(Vector3 target)
	{
		PacketHandler.sendPacketToClients(ICBMCore.PACKET_TILE.getPacketWithID(FIRING_EVENT_PACKET_ID, this, target), this.getWorldObj(), new Vector3(this), 100);
	}

	@Override
	public boolean onReceivePacket(int id, ByteArrayDataInput data, EntityPlayer player, Object... extra)
	{
		if (!super.onReceivePacket(id, data, player, extra))
		{
			if (id == DESCRIPTION_PACKET_ID)
			{
				this.sentry = SentryRegistry.constructSentry(data.readUTF(), this);
				this.getYawServo().setRotation(data.readFloat());
				this.getPitchServo().setRotation(data.readFloat());
				return true;
			}
			if (id == ROTATION_PACKET_ID)
			{
				this.getYawServo().setRotation(data.readFloat());
				this.getPitchServo().setRotation(data.readFloat());
				return true;
			}
			if (id == FIRING_EVENT_PACKET_ID)
			{
				this.getSentry().fire(new Vector3(data.readDouble(), data.readDouble(), data.readDouble()));
				return true;
			}

			return false;
		}
		return true;
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	@Override
	public void setDirection(ForgeDirection direction)
	{
		this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, direction.ordinal(), 3);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		this.getInventory().save(nbt);
		NBTTagCompound perm_tag = new NBTTagCompound();
		this.getAccessProfile().save(perm_tag);
		nbt.setCompoundTag("permissions", perm_tag);

		if (this.getSentry() != null)
		{
			NBTTagCompound sentrySave = new NBTTagCompound();
			this.getSentry().save(sentrySave);
			nbt.setCompoundTag(ISentry.SENTRY_OBJECT_SAVE, sentrySave);
		}

		if (unlocalizedName != null && !unlocalizedName.isEmpty())
			nbt.setString("unlocalizedName", unlocalizedName);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		this.unlocalizedName = nbt.getString("unlocalizedName");
		this.getInventory().load(nbt);
		this.getAccessProfile().load(nbt.getCompoundTag("permissions"));

		if (nbt.hasKey(ISentry.SENTRY_OBJECT_SAVE))
		{
			NBTTagCompound tag = nbt.getCompoundTag(ISentry.SENTRY_OBJECT_SAVE);
			this.saveManagerSentryKey = tag.getString(ISentry.SENTRY_SAVE_ID);
			this.sentry = SentryRegistry.constructSentry(saveManagerSentryKey, this);

			if (this.getSentry() != null)
			{
				this.getSentry().load(nbt);
			}
		}

	}

	@Override
	public IExternalInventoryBox getInventory()
	{
		return this.inventory;
	}

	@Override
	public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
	{
		return false;
	}

	@Override
	public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
	{
		return false;
	}

	@Override
	public AutoServo getYawServo()
	{
		if (this.yawMotor == null)
			this.yawMotor = new AutoServo(yawData[0], yawData[1], yawData[2]);
		return this.yawMotor;
	}

	@Override
	public AutoServo getPitchServo()
	{
		if (this.pitchMotor == null)
			this.pitchMotor = new AutoServo(pitchData[0], pitchData[1], pitchData[2]);
		return this.pitchMotor;
	}

	public Sentry getSentry()
	{
		if (this.sentry == null)
			this.sentry = SentryRegistry.constructSentry(saveManagerSentryKey, this);
		return this.sentry;
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		if (entityPlayer != null)
		{
			entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("[Debug]" + (this.worldObj.isRemote ? "Client  " : "Server") + " Sentry: " + (this.getSentry() == null ? "null" : SentryRegistry.getID(this.getSentry()))));
			if (!entityPlayer.isSneaking())
			{
				if (this.getSentry() instanceof MountedSentry && this.sentryEntity != null)
				{
					if (this.sentryEntity.riddenByEntity instanceof EntityPlayer)
					{
						if (!this.worldObj.isRemote)
						{
							PacketHandler.sendPacketToClients(this.getRotationPacket());
						}
						return true;
					}
					this.mount(entityPlayer);
				}

			}

			return true;
		}
		return false;
	}

	public void mount(EntityPlayer entityPlayer)
	{
		if (!this.worldObj.isRemote)
		{
			entityPlayer.rotationYaw = this.getYawServo().getRotation();
			entityPlayer.rotationPitch = this.getPitchServo().getRotation();
			entityPlayer.mountEntity(this.sentryEntity);

		}

	}

	public EntitySentryFake getFakeEntity()
	{
		return this.sentryEntity;
	}

	public void setFakeEntity(EntitySentryFake entitySentryFake)
	{
		this.sentryEntity = entitySentryFake;
	}

	@Override
	public World world()
	{
		return this.getWorldObj();
	}

	@Override
	public double x()
	{
		return this.xCoord + 0.5;
	}

	@Override
	public double y()
	{
		return this.yCoord + 0.5;
	}

	@Override
	public double z()
	{
		return this.zCoord + 0.5;
	}

	@Override
	public float yaw()
	{
		return this.getYawServo().getRotation();
	}

	@Override
	public float pitch()
	{
		return this.getPitchServo().getRotation();
	}

	@Override
	public boolean canUse(String node, EntityPlayer player)
	{
		return this.getAccessProfile().getOwnerGroup().isMemeber(player.username);
	}

}
