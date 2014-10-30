package icbm.explosion.machines;

import icbm.core.ICBMCore;
import icbm.core.prefab.TileICBM;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.items.ItemMissile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import resonant.api.IRotatable;
import resonant.api.ITier;
import resonant.lib.multiblock.IBlockActivate;
import resonant.lib.multiblock.IMultiBlock;
import resonant.lib.network.IPacketReceiver;
import resonant.lib.utility.LanguageUtility;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;

public class TileMissileAssembler extends TileICBM implements IMultiBlock, ITier, IRotatable, IPacketReceiver, IInventory, IBlockActivate
{
    public int tier = -1, missileID = -1;
    /** Side placed on */
    public ForgeDirection placedSide = ForgeDirection.UP;
    /** 0 - 3 of rotation on the given side */
    public byte rotationSide = 0;
    public boolean rotating = false;

    EntityMissile missile;
    private ItemStack[] containingItems = new ItemStack[1];

    public TileMissileAssembler()
    {
        setEnergyHandler(new EnergyStorageHandler());
    }

    @Override
    public void initiate()
    {
        this.onInventoryChanged();
    }

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        return getMultiBlockVectors(placedSide, rotationSide);
    }

    public static Vector3[] getMultiBlockVectors(ForgeDirection side, byte rot)
    {
        // rotation doesn't really effect the multi block too much however placed side does
        if (side == ForgeDirection.UP || side == ForgeDirection.DOWN)
        {
            // line up on the x
            if (rot == 0 || rot == 2)
            {
                return new Vector3[] { new Vector3(1, 0, 0), new Vector3(-1, 0, 0) };
            }
            // lined up on the z
            return new Vector3[] { new Vector3(0, 0, 1), new Vector3(0, 0, -1) };
        }
        else
        {
            // Lined up with x or z
            if (rot == 0 || rot == 2)
            {
                if (side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH)
                {
                    return new Vector3[] { new Vector3(-1, 0, 0), new Vector3(1, 0, 0) };
                }
                else if (side == ForgeDirection.EAST || side == ForgeDirection.WEST)
                {
                    return new Vector3[] { new Vector3(0, 0, -1), new Vector3(0, 0, 1) };
                }
            }
            // Lined up with the Y
            return new Vector3[] { new Vector3(0, 1, 0), new Vector3(0, -1, 0) };
        }
    }

    @Override
    public int getTier()
    {
        if (tier == -1)
        {
            this.setTier(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        }
        return tier;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier & 3;
    }

    @Override
    public ForgeDirection getDirection()
    {
        // direction is actually based on the rotation of the object on the side of a block. This
        // way the assembly line rotation block will rotate it correctly. As well for wrench support

        if (this.rotationSide == 0)
        {
            return ForgeDirection.NORTH;
        }
        else if (this.rotationSide == 2)
        {
            return ForgeDirection.SOUTH;
        }
        else if (this.rotationSide == 1)
        {
            return ForgeDirection.EAST;
        }
        else
        {
            return ForgeDirection.WEST;
        }
    }

    @Override
    public void setDirection(ForgeDirection direction)
    {
        byte rot = 0;

        if (direction == ForgeDirection.NORTH)
        {
            rot = 0;
        }
        else if (direction == ForgeDirection.SOUTH)
        {
            rot = 2;
        }
        else if (direction == ForgeDirection.EAST)
        {
            rot = 1;
        }
        else
        {
            rot = 3;
        }
        if (BlockMissileAssembler.canRotateBlockTo(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.placedSide, rot))
        {
            this.rotationSide = rot;
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public void setPlacedSide(ForgeDirection side)
    {
        this.placedSide = side;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setRotation(byte rot)
    {
        this.rotationSide = rot;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.rotationSide = nbt.getByte("rotationSide");
        this.placedSide = ForgeDirection.getOrientation(nbt.getByte("placedSide"));

        NBTTagList var2 = nbt.getTagList("Items");

        this.containingItems = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.containingItems.length)
            {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("rotationSide", this.rotationSide);
        nbt.setByte("placedSide", (byte) this.placedSide.ordinal());

        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.containingItems.length; ++var3)
        {
            if (this.containingItems[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        nbt.setTag("Items", var2);
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        if (entityPlayer.inventory.getCurrentItem() != null && this.getStackInSlot(0) == null)
        {
            if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemMissile)
            {
                this.setInventorySlotContents(0, entityPlayer.inventory.getCurrentItem());
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                this.onInventoryChanged();
                return true;
            }
        }

        if(!this.worldObj.isRemote)
        	entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        if (this.worldObj.isRemote)
        {
            byte id = data.readByte();

            if (id == 0)
            {
                this.rotationSide = data.readByte();
                this.placedSide = ForgeDirection.getOrientation(data.readByte());
                this.missileID = data.readInt();
                this.worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, ((byte) 0), this.rotationSide, ((byte) this.placedSide.ordinal()), this.missileID);
    }

    @Override
    public void onInventoryChanged()
    {
        super.onInventoryChanged();
        if (!this.worldObj.isRemote)
        {
            if (this.getStackInSlot(0) != null && this.getStackInSlot(0).getItem() instanceof ItemMissile)
            {
                missileID = this.getStackInSlot(0).getItemDamage();
            }
            else
            {
                missileID = -1;
            }
        }
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    /** Returns the number of slots in the inventory. */
    @Override
    public int getSizeInventory()
    {
        return this.containingItems.length;
    }

    /** Returns the stack in slot i */
    @Override
    public ItemStack getStackInSlot(int par1)
    {
        return this.containingItems[par1];
    }

    /** Decrease the size of the stack in slot (first int arg) by the amount of the second int arg.
     * Returns the new stack. */
    @Override
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.containingItems[par1] != null)
        {
            ItemStack var3;

            if (this.containingItems[par1].stackSize <= par2)
            {
                var3 = this.containingItems[par1];
                this.containingItems[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.containingItems[par1].splitStack(par2);

                if (this.containingItems[par1].stackSize == 0)
                {
                    this.containingItems[par1] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /** When some containers are closed they call this on each slot, then drop whatever it returns as
     * an EntityItem - like when you close a workbench GUI. */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.containingItems[par1] != null)
        {
            ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /** Sets the given item stack to the specified slot in the inventory (can be crafting or armor
     * sections). */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.containingItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /** Returns the name of the inventory. */
    @Override
    public String getInvName()
    {
        return LanguageUtility.getLocal("gui.assembler.name");
    }

    /** Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be
     * extended. *Isn't this more of a set than a get?* */
    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    /** Do not make give this method the name canInteractWith because it clashes with Container */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openChest()
    {
    }

    @Override
    public void closeChest()
    {
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public boolean isInvNameLocalized()
    {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemStack)
    {
        return itemStack.getItem() instanceof ItemMissile;
    }

}
