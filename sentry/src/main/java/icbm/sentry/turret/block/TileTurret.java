package icbm.sentry.turret.block;

import icbm.core.ICBMCore;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.TurretRegistry;
import icbm.sentry.turret.ai.EulerServo;
import icbm.sentry.turret.mount.MountedTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.access.AccessProfile;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.tile.IRotatable;
import calclavia.lib.terminal.TileTerminal;
import calclavia.lib.utility.inventory.ExternalInventory;
import calclavia.lib.utility.inventory.IExternalInventory;
import calclavia.lib.utility.inventory.IExternalInventoryBox;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * TurretProvider tile to host turret objects.
 * 
 * @author Darkguardsman, tgame14
 */
public class TileTurret extends TileTerminal implements IProfileContainer, IRotatable, IExternalInventory, IBlockActivate, ITurretProvider
{
	protected static final int ROTATION_PACKET_ID = 3;
	protected static final int SENTRY_TYPE_PACKET_ID = 4;
	protected static final int DESCRIPTION_PACKET_ID = 5;
	protected static final int FIRING_EVENT_PACKET_ID = 6;

	/** TURRET AIM & ROTATION HELPER */
	public EntityMountableDummy sentryEntity;
	/** Profile that control access properties for users */
	protected AccessProfile accessProfile;
	/** Sentries inventory used for upgrades and ammo */
	protected IExternalInventoryBox inventory;
	/** Sentry instance used to define the visuals and weapons of the sentry */
	protected Turret turret;

	private String unlocalizedName = "err";
	private String saveManagerSentryKey;

	public TileTurret()
	{
		super();
		this.inventory = new ExternalInventory(this, 8);
		this.energy = new EnergyStorageHandler(1000);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		EulerServo prevServo = (EulerServo) getTurret().getServo().clone();

		if (getTurret() != null)
		{
			getTurret().update();
		}

		// TODO Instead of sending the current rotation, send the target because the client can
		// "rotate itself". -- Calclavia
		if (!worldObj.isRemote)
		{
			if (!prevServo.equals(getTurret().getServo()))
			{
				PacketHandler.sendPacketToClients(this.getRotationPacket(), this.getWorldObj(), new Vector3(this), 60);
			}
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
		return ICBMCore.PACKET_TILE.getPacketWithID(DESCRIPTION_PACKET_ID, this, saveManagerSentryKey, getTurret().getServo().yaw, getTurret().getServo().pitch);
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
		return ICBMCore.PACKET_TILE.getPacketWithID(ROTATION_PACKET_ID, this, getTurret().getServo().yaw, getTurret().getServo().pitch);
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
				turret = TurretRegistry.constructSentry(data.readUTF(), this);
				getTurret().getServo().yaw = data.readDouble();
				getTurret().getServo().pitch = data.readDouble();
				return true;
			}
			if (id == ROTATION_PACKET_ID)
			{
				getTurret().getServo().yaw = data.readDouble();
				getTurret().getServo().pitch = data.readDouble();
				return true;
			}
			if (id == FIRING_EVENT_PACKET_ID)
			{
				getTurret().fire(new Vector3(data.readDouble(), data.readDouble(), data.readDouble()));
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

		if (this.getTurret() != null)
		{
			NBTTagCompound sentrySave = new NBTTagCompound();
			this.getTurret().save(sentrySave);
			nbt.setCompoundTag(ITurret.SENTRY_OBJECT_SAVE, sentrySave);
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

		if (nbt.hasKey(ITurret.SENTRY_OBJECT_SAVE))
		{
			NBTTagCompound tag = nbt.getCompoundTag(ITurret.SENTRY_OBJECT_SAVE);
			this.saveManagerSentryKey = tag.getString(ITurret.SENTRY_SAVE_ID);
			this.turret = TurretRegistry.constructSentry(saveManagerSentryKey, this);

			if (this.getTurret() != null)
			{
				this.getTurret().load(nbt);
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

	public Turret getTurret()
	{
		if (this.turret == null)
			this.turret = TurretRegistry.constructSentry(saveManagerSentryKey, this);
		return this.turret;
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		if (entityPlayer != null)
		{
			if (!entityPlayer.isSneaking())
			{
				if (this.getTurret() instanceof MountedTurret && this.sentryEntity != null)
				{
					if (this.sentryEntity.riddenByEntity instanceof EntityPlayer)
					{
						if (!this.worldObj.isRemote)
						{
							PacketHandler.sendPacketToClients(this.getRotationPacket());
						}
						return true;
					}

					mount(entityPlayer);
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
			entityPlayer.rotationYaw = (float) getTurret().getServo().yaw;
			entityPlayer.rotationPitch = (float) getTurret().getServo().pitch;
			entityPlayer.mountEntity(this.sentryEntity);
		}
	}

	public EntityMountableDummy getFakeEntity()
	{
		return this.sentryEntity;
	}

	public void setFakeEntity(EntityMountableDummy entitySentryFake)
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
		return xCoord + 0.5;
	}

	@Override
	public double y()
	{
		return yCoord + 0.5;
	}

	@Override
	public double z()
	{
		return zCoord + 0.5;
	}

	@Override
	public boolean canUse(String node, EntityPlayer player)
	{
		return this.getAccessProfile().getOwnerGroup().isMemeber(player.username);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 2, zCoord + 2);
	}

}
