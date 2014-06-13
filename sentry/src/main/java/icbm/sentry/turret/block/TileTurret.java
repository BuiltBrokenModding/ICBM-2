package icbm.sentry.turret.block;

import icbm.core.ICBMCore;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.platform.TileTurretPlatform;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.TurretRegistry;
import icbm.sentry.turret.TurretType;
import icbm.sentry.turret.mounted.TurretMounted;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import resonant.api.IExternalInventory;
import resonant.api.IExternalInventoryBox;
import resonant.api.IRotatable;
import resonant.lib.access.AccessProfile;
import resonant.lib.access.AccessUser;
import resonant.lib.access.IProfileContainer;
import resonant.lib.multiblock.IBlockActivate;
import resonant.lib.network.PacketHandler;
import resonant.lib.prefab.terminal.TileTerminal;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** TurretProvider tile to host turret objects.
 * 
 * @author Darkguardsman, tgame14 */
public class TileTurret extends TileTerminal implements IProfileContainer, IRotatable, IExternalInventory, IBlockActivate, ITurretProvider
{
    protected static final int ROTATION_PACKET_ID = 3;
    protected static final int SENTRY_TYPE_PACKET_ID = 4;
    protected static final int DESCRIPTION_PACKET_ID = 5;
    protected static final int FIRING_EVENT_PACKET_ID = 6;
    protected static final int ENERGY_PACKET_ID = 7;
    protected static final int PROFILE_PACKET_ID = 8;
    protected static final int PROFILE_ADD_USER_PACKET_ID = 9;

    /** Profile that control access properties for users */
    protected AccessProfile accessProfile;
    /** Sentry instance used to define the visuals and weapons of the sentry */
    protected Turret turret;

    private String unlocalizedName;
    private String saveManagerSentryKey;

    private long turretPrevEnergy;

    public TileTurret()
    {
        this.unlocalizedName = "err";
        this.turretPrevEnergy = 0;

    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (getTurret() != null)
        {
            getTurret().update();

            if (!worldObj.isRemote)
            {
                if (getTurret().getServo().hasChanged)
                {
                    PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 60, worldObj.provider.dimensionId, this.getRotationPacket());
                }
                if (this.turretPrevEnergy != getTurret().battery.getEnergy())
                {
                    PacketHandler.sendPacketToClients(this.getEnergyPacket(), this.getWorldObj());
                    this.turretPrevEnergy = getTurret().battery.getEnergy();
                }
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
        if (profile != null)
        {
            profile.addContainer(this);
        }
    }

    @Override
    public boolean canAccess(String username)
    {
        return this.getAccessProfile().getUserAccess(username).getGroup() != null;
    }

    @Override
    public void onProfileChange()
    {
        PacketHandler.sendPacketToClients(this.getProfilePacket(), worldObj, this, 60);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return this.getNBTPacket();
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

    public Packet getProfilePacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        this.getAccessProfile().save(tag);
        return ICBMCore.PACKET_TILE.getPacketWithID(PROFILE_PACKET_ID, this, tag);
    }

    @Override
    public void sendFireEventToClient(IVector3 target)
    {
        PacketHandler.sendPacketToClients(ICBMCore.PACKET_TILE.getPacketWithID(FIRING_EVENT_PACKET_ID, this, target), this.getWorldObj(), this, 100);
    }

    public Packet getEnergyPacket()
    {
        return ICBMCore.PACKET_TILE.getPacketWithID(ENERGY_PACKET_ID, this, getTurret().getEnergy(ForgeDirection.UNKNOWN));
    }

    @Override
    public boolean onReceivePacket(int id, ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        if (!super.onReceivePacket(id, data, player, extra))
        {
            try
            {
                if (this.worldObj.isRemote)
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
                        getTurret().fire(new Vector3(data));
                        return true;
                    }
                    if (id == ENERGY_PACKET_ID)
                    {
                        getTurret().setEnergy(ForgeDirection.UNKNOWN, data.readLong());
                        return true;
                    }

                    if (id == PROFILE_PACKET_ID)
                    {
                        this.getAccessProfile().load(PacketHandler.readNBTTagCompound(data));
                        return true;
                    }
                }
                return false;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return true;
            }
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
        if (nbt.hasKey("permissions"))
            this.setAccessProfile(new AccessProfile((nbt.getCompoundTag("permissions"))));

        if (nbt.hasKey(ITurret.SENTRY_OBJECT_SAVE))
        {
            NBTTagCompound tag = nbt.getCompoundTag(ITurret.SENTRY_OBJECT_SAVE);
            this.saveManagerSentryKey = tag.getString(ITurret.SENTRY_TYPE_SAVE_ID);
            this.turret = TurretRegistry.constructSentry(saveManagerSentryKey, this);

            if (this.getTurret() != null)
            {
                this.getTurret().load(tag);
            }
        }
        else
        {
            this.saveManagerSentryKey = TurretType.GUN_TURRET.getId();
        }

    }

    @Override
    public IExternalInventoryBox getInventory()
    {
        TileEntity tile = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);

        if (tile instanceof TileTurretPlatform)
        {
            return ((TileTurretPlatform) tile).getInventory();
        }

        return null;
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
    public Turret getTurret()
    {
        if (this.turret == null)
            this.turret = TurretRegistry.constructSentry(saveManagerSentryKey, (ITurretProvider) this);

        return this.turret;
    }

    /** temp
     * 
     * @param str */
    private static void debug(String str)
    {
        if (false)
            ICBMCore.LOGGER.warning(str);
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        if (entityPlayer != null)
        {
            if (!entityPlayer.isSneaking())
            {
                if (this.getTurret() instanceof TurretMounted && ((TurretMounted) this.getTurret()).getFakeEntity() != null)
                {
                    if (((TurretMounted) this.getTurret()).getFakeEntity().riddenByEntity instanceof EntityPlayer)
                    {
                        if (!this.worldObj.isRemote)
                        {
                            PacketHandler.sendPacketToClients(this.getRotationPacket());
                        }
                        return true;
                    }
                    if (!this.worldObj.isRemote)
                    {
                        entityPlayer.rotationYaw = (float) getTurret().getServo().yaw;
                        entityPlayer.rotationPitch = (float) getTurret().getServo().pitch;
                        entityPlayer.mountEntity(((TurretMounted) this.getTurret()).getFakeEntity());
                    }
                }

            }

            return true;
        }
        return false;
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
        AccessUser user = this.getAccessProfile().getUserAccess(player.username);
        assert user != null : "TileTurret canUse() user returned null for player " + player.username;

        if (user != null && user.getGroup() != null)
        {
            return this.getAccessProfile().getUserAccess(player.username).hasNode(node);
        }
        return false;
    }

    @Override
    public void onInventoryChanged()
    {
        super.onInventoryChanged();
        this.getTurret().onInventoryChanged();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        //TODO: change this based on model size
        return AxisAlignedBB.getAABBPool().getAABB(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 2, zCoord + 2);
    }
}
