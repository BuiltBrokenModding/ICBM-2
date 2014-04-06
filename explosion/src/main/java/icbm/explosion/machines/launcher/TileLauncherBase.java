package icbm.explosion.machines.launcher;

import icbm.Settings;
import icbm.core.ICBMCore;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.ex.Ex;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.items.ItemMissile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorHelper;
import calclavia.api.icbm.ILauncherContainer;
import calclavia.api.icbm.ILauncherController;
import calclavia.api.icbm.IMissile;
import calclavia.api.icbm.ITier;
import calclavia.api.icbm.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import calclavia.api.icbm.explosion.ExplosiveType;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.multiblock.fake.IMultiBlock;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.tile.IRotatable;
import calclavia.lib.prefab.tile.TileExternalInventory;
import calclavia.lib.utility.LanguageUtility;

import com.google.common.io.ByteArrayDataInput;

/** This tile entity is for the base of the missile launcher
 * 
 * @author Calclavia */
public class TileLauncherBase extends TileExternalInventory implements IPacketReceiver, ILauncherContainer, IRotatable, ITier, IMultiBlock, IBlockActivate
{
    // The missile that this launcher is holding
    public IMissile missile = null;

    // The connected missile launcher frame
    public TileLauncherFrame supportFrame = null;

    // The tier of this launcher base
    private int tier = 0;

    private ForgeDirection facingDirection = ForgeDirection.NORTH;

    private boolean packetGengXin = true;

    /** Returns the name of the inventory. */
    @Override
    public String getInvName()
    {
        return LanguageUtility.getLocal("gui.launcherBase.name");
    }

    /** Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner
     * uses this to count ticks and creates a new spawn inside its implementation. */
    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.supportFrame == null)
        {
            for (byte i = 2; i < 6; i++)
            {
                Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
                position.translate(ForgeDirection.getOrientation(i));

                TileEntity tileEntity = this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ());

                if (tileEntity instanceof TileLauncherFrame)
                {
                    this.supportFrame = (TileLauncherFrame) tileEntity;
                    this.supportFrame.setDirection(VectorHelper.getOrientationFromSide(ForgeDirection.getOrientation(i), ForgeDirection.NORTH));
                }
            }
        }
        else
        {
            if (this.supportFrame.isInvalid())
            {
                this.supportFrame = null;
            }
            else if (this.packetGengXin || this.ticks % (20 * 30) == 0 && this.supportFrame != null && !this.worldObj.isRemote)
            {
                PacketHandler.sendPacketToClients(this.supportFrame.getDescriptionPacket());
            }
        }

        if (!this.worldObj.isRemote)
        {
            this.setMissile();

            if (this.packetGengXin || this.ticks % (20 * 30) == 0)
            {
                PacketHandler.sendPacketToClients(this.getDescriptionPacket());
                this.packetGengXin = false;
            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, (byte) this.facingDirection.ordinal(), this.tier);
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        try
        {

            this.facingDirection = ForgeDirection.getOrientation(data.readByte());
            this.tier = data.readInt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setMissile()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.getStackInSlot(0) != null)
            {
                if (this.getStackInSlot(0).getItem() instanceof ItemMissile)
                {
                    int explosiveID = this.getStackInSlot(0).getItemDamage();

                    if (ExplosiveRegistry.get(explosiveID) instanceof Ex)
                    {
                        Ex missile = (Ex) ExplosiveRegistry.get(explosiveID);

                        ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ExplosiveType.AIR, missile);
                        MinecraftForge.EVENT_BUS.post(evt);

                        if (!evt.isCanceled())
                        {
                            if (this.missile == null)
                            {
                                Vector3 startingPosition = new Vector3((this.xCoord + 0.5f), (this.yCoord + 1.8f), (this.zCoord + 0.5f));
                                this.missile = new EntityMissile(this.worldObj, startingPosition, new Vector3(this), explosiveID);
                                this.worldObj.spawnEntityInWorld((Entity) this.missile);
                                return;
                            }
                            else
                            {
                                if (this.missile.getExplosiveType().getID() == explosiveID)
                                {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (this.missile != null)
            {
                ((Entity) this.missile).setDead();
            }

            this.missile = null;
        }
    }

    /** Launches the missile
     * 
     * @param target - The target in which the missile will land in */
    public void launchMissile(Vector3 target, int gaoDu)
    {
        // Apply inaccuracy
        float inaccuracy;

        if (this.supportFrame != null)
        {
            inaccuracy = this.supportFrame.getInaccuracy();
        }
        else
        {
            inaccuracy = 30f;
        }

        inaccuracy *= (float) Math.random() * 2 - 1;

        target.x += inaccuracy;
        target.z += inaccuracy;

        this.decrStackSize(0, 1);
        this.missile.launch(target, gaoDu);
        this.missile = null;
    }

    // Checks if the missile target is in range
    public boolean isInRange(Vector3 target)
    {
        if (target != null)
            return !shiTaiYuan(target) && !shiTaiJin(target);

        return false;
    }

    /** Checks to see if the target is too close.
     * 
     * @param target
     * @return */
    public boolean shiTaiJin(Vector3 target)
    {
        // Check if it is greater than the minimum range
        if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < 10)
        {
            return true;
        }

        return false;
    }

    // Is the target too far?
    public boolean shiTaiYuan(Vector3 target)
    {
        // Checks if it is greater than the maximum range for the launcher base
        if (this.tier == 0)
        {
            if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < Settings.DAO_DAN_ZUI_YUAN / 10)
            {
                return false;
            }
        }
        else if (this.tier == 1)
        {
            if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < Settings.DAO_DAN_ZUI_YUAN / 5)
            {
                return false;
            }
        }
        else if (this.tier == 2)
        {
            if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < Settings.DAO_DAN_ZUI_YUAN)
            {
                return false;
            }
        }

        return true;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.tier = nbt.getInteger("tier");
        this.facingDirection = ForgeDirection.getOrientation(nbt.getByte("facingDirection"));
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("tier", this.tier);
        nbt.setByte("facingDirection", (byte) this.facingDirection.ordinal());
    }

    @Override
    public int getTier()
    {
        return this.tier;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier;
    }

    @Override
    public boolean onActivated(EntityPlayer player)
    {
        if (player.inventory.getCurrentItem() != null)
        {
            if (player.inventory.getCurrentItem().getItem() instanceof ItemMissile)
            {
                if (this.getStackInSlot(0) == null)
                {

                    this.setInventorySlotContents(0, player.inventory.getCurrentItem());
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    return true;
                }
                else
                {
                    ItemStack player_held = player.inventory.getCurrentItem();
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, this.getStackInSlot(0));
                    this.setInventorySlotContents(0, player_held);
                    return true;
                }
            }
        }
        else if (this.getStackInSlot(0) != null)
        {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, this.getStackInSlot(0));
            this.setInventorySlotContents(0, null);
            return true;
        }

        return true;
    }

    @Override
    public void invalidate()
    {
        if (this.missile != null)
        {
            ((Entity) this.missile).setDead();
        }

        super.invalidate();
    }

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        if (this.facingDirection == ForgeDirection.SOUTH || this.facingDirection == ForgeDirection.NORTH)
        {
            return new Vector3[] { new Vector3(1, 0, 0), new Vector3(1, 1, 0), new Vector3(1, 2, 0), new Vector3(-1, 0, 0), new Vector3(-1, 1, 0), new Vector3(-1, 2, 0) };
        }
        else
        {
            return new Vector3[] { new Vector3(0, 0, 1), new Vector3(0, 1, 1), new Vector3(0, 2, 1), new Vector3(0, 0, -1), new Vector3(0, 1, -1), new Vector3(0, 2, -1) };
        }
    }

    @Override
    public ForgeDirection getDirection()
    {
        return this.facingDirection;
    }

    @Override
    public void setDirection(ForgeDirection facingDirection)
    {
        this.facingDirection = facingDirection;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot == 0 && stack.getItem() instanceof ItemMissile;
    }

    @Override
    public IMissile getContainingMissile()
    {
        return this.missile;
    }

    @Override
    public void setContainingMissile(IMissile missile)
    {
        this.missile = missile;
    }

    @Override
    public ILauncherController getController()
    {
        for (byte i = 2; i < 6; i++)
        {
            Vector3 position = new Vector3(this).translate(ForgeDirection.getOrientation(i));

            TileEntity tileEntity = position.getTileEntity(this.worldObj);

            if (tileEntity instanceof ILauncherController)
            {
                return (ILauncherController) tileEntity;
            }
        }

        return null;
    }

    @Override
    public int[] getMissileSlots()
    {
        return new int[] { 0 };
    }
}
