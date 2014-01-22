package icbm.sentry.turret;

import icbm.api.sentry.IServo;
import icbm.api.sentry.IWeaponSystem;
import icbm.core.ICBMCore;
import icbm.sentry.interfaces.ISentry;
import icbm.sentry.task.LookHelper;
import icbm.sentry.task.RotationHelper;
import icbm.sentry.task.ServoMotor;
import icbm.sentry.turret.sentries.Sentry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.access.AccessProfile;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.prefab.tile.IRotatable;
import calclavia.lib.terminal.TileTerminal;
import calclavia.lib.utility.inventory.ExternalInventory;
import calclavia.lib.utility.inventory.IExternalInventory;
import calclavia.lib.utility.inventory.IExternalInventoryBox;

/** @author Darkguardsman */
public class TileSentry extends TileTerminal implements IProfileContainer, IRotatable, IExternalInventory, ISentry
{
    /** Profile that control access properties for users */
    protected AccessProfile accessProfile;
    /** Sentries inventory used for upgrades and ammo */
    protected IExternalInventoryBox inventory;
    /** Sentry instance used to define the visuals and weapons of the sentry */
    protected Sentry sentry;
    /** Helper class that deals with rotation */
    public RotationHelper rotationHelper;
    /** TURRET AIM & ROTATION HELPER */
    public LookHelper lookHelper;
    /** Yaw servo rotation */
    public ServoMotor yawMotor;
    /** Pitch servo rotation */
    public ServoMotor pitchMotor;

    public TileSentry()
    {
        this.inventory = new ExternalInventory(this, 8);
        this.energy = new EnergyStorageHandler(1000);
        this.rotationHelper = new RotationHelper(this);
        lookHelper = new LookHelper(this);
        this.yawMotor = new ServoMotor(360, 0);
        this.pitchMotor = new ServoMotor(35, -35);
    }

    public void loadSentry(Sentry sentry)
    {
        this.sentry = sentry;
        if (sentry != null)
        {
            this.energy.setCapacity(sentry.getEnergyCapacity());
            this.energy.setMaxExtract(sentry.getEnergyPerTick());
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
        return ICBMCore.PACKET_TILE.getPacket(this, this.getPacketData(0).toArray());
    }

    @Override
    public Packet getTerminalPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, this.getPacketData(1).toArray());
    }

    @Override
    public Packet getCommandPacket(String username, String cmdInput)
    {
        return ICBMCore.PACKET_TILE.getPacket(this, this.getPacketData(2).toArray());
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
    public IWeaponSystem[] getWeaponSystems()
    {
        return this.sentry.getWeaponSystems();
    }

    @Override
    public boolean canSupportWeaponSystem(int slot, IWeaponSystem system)
    {
        return this.sentry.canSupportWeaponSystem(slot, system);
    }

    @Override
    public boolean addWeaponSystem(int slot, IWeaponSystem system)
    {
        return this.sentry.addWeaponSystem(slot, system);
    }

    @Override
    public boolean removeWeaponSystem(int slot, IWeaponSystem system)
    {
        return this.sentry.removeWeaponSystem(slot, system);
    }

    @Override
    public IServo getYawServo()
    {
        return null;
    }

    @Override
    public IServo getPitchServo()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TileEntity getPlatform()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3 getAimOffset()
    {
        return sentry.getAimOffset();
    }
}
