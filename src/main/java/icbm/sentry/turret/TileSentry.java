package icbm.sentry.turret;

import icbm.api.sentry.IGyroMotor;
import icbm.api.sentry.IServo;
import icbm.core.ICBMCore;
import icbm.sentry.turret.mount.MountedSentry;
import icbm.sentry.turret.sentries.AutoSentry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import calclavia.lib.access.AccessProfile;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.tile.IRotatable;
import calclavia.lib.terminal.TileTerminal;
import calclavia.lib.utility.inventory.ExternalInventory;
import calclavia.lib.utility.inventory.IExternalInventory;
import calclavia.lib.utility.inventory.IExternalInventoryBox;

/** @author Darkguardsman */
public class TileSentry extends TileTerminal implements IProfileContainer, IRotatable, IGyroMotor, IExternalInventory, IBlockActivate
{
    protected static final int ROTATION_PACKET_ID = 3;

    /** Profile that control access properties for users */
    protected AccessProfile accessProfile;

    /** Sentries inventory used for upgrades and ammo */
    protected IExternalInventoryBox inventory;

    /** Sentry instance used to define the visuals and weapons of the sentry */
    protected Sentry sentry;

    /** TURRET AIM & ROTATION HELPER */
    public LookHelper lookHelper;

    /** Yaw servo rotation */
    public AutoServo yawMotor;
    /** Pitch servo rotation */
    public AutoServo pitchMotor;

    public EntitySentryFake sentryEntity;

    public TileSentry()
    {
        this.inventory = new ExternalInventory(this, 8);
        this.energy = new EnergyStorageHandler(1000);
        lookHelper = new LookHelper(this);
        this.yawMotor = new AutoServo(360, 0, 5);
        this.pitchMotor = new AutoServo(35, -35, 5);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (this.getSentry() instanceof MountedSentry)
        {
            if (this.sentryEntity == null || this.sentryEntity.isDead)
            {
                this.sentryEntity = new EntitySentryFake(this, true);
                this.worldObj.spawnEntityInWorld(this.sentryEntity);
            }
            //TODO set up handling for non-player entities
            if (this.sentryEntity.riddenByEntity instanceof EntityPlayer)
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
        if (this.getSentry() instanceof AutoSentry)
        {
            float prevYaw = this.getYawServo().getRotation();
            float prevPitch = this.getPitchServo().getRotation();
            this.yawMotor.update();
            this.pitchMotor.update();
            if (prevYaw != this.getYawServo().getRotation() || prevPitch != this.getPitchServo().getRotation())
            {
                PacketHandler.sendPacketToClients(this.getRotationPacket());
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
        return accessProfile.getUserAccess(username) != null;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacketWithID(NBT_PACKET_ID, this, this.getPacketData(0).toArray());
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
    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
    }

    @Override
    public void setDirection(ForgeDirection direection)
    {

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.getInventory().load(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.getInventory().save(nbt);
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
        return this.yawMotor;
    }

    @Override
    public AutoServo getPitchServo()
    {
        return this.pitchMotor;
    }

    public Sentry getSentry()
    {
        return this.sentry;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        if (entityPlayer != null)
        {
            entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Sentries are indev and don't currently have a functioning GUI"));

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
}
