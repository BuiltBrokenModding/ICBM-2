package icbm.explosion.machines;

import icbm.Reference;
import icbm.core.prefab.TileICBM;
import icbm.explosion.ICBMExplosion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import resonant.api.IRotatable;
import resonant.lib.multiblock.IBlockActivate;
import resonant.lib.network.IPacketReceiver;
import resonant.lib.utility.LanguageUtility;
import universalelectricity.api.energy.EnergyStorageHandler;

import com.google.common.io.ByteArrayDataInput;

/** Missile Coordinator
 * 
 * @author Calclavia */
public class TileMissileCoordinator extends TileICBM implements IPacketReceiver, IRotatable, IInventory, IBlockActivate
{
    private byte fangXiang = 3;
    protected ItemStack[] containingItems = new ItemStack[this.getSizeInventory()];

    public TileMissileCoordinator()
    {
        setEnergyHandler(new EnergyStorageHandler(0));
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack getStackInSlot(int par1)
    {
        return this.containingItems[par1];
    }

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

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.containingItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
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
    public boolean isInvNameLocalized()
    {
        return true;
    }

    @Override
    public String getInvName()
    {
        return LanguageUtility.getLocal("gui.coordinator.name");
    }

    /** @return Returns if a specific slot is valid to input a specific itemStack. */
    public boolean isStackValidForInputSlot(int slot, ItemStack itemStack)
    {
        if (this.getStackInSlot(slot) == null)
        {
            return true;
        }
        else
        {
            if (this.getStackInSlot(slot).stackSize + 1 <= 64)
            {
                return this.getStackInSlot(slot).isItemEqual(itemStack);
            }
        }

        return false;
    }

    public void incrStackSize(int slot, ItemStack itemStack)
    {
        if (this.getStackInSlot(slot) == null)
        {
            this.setInventorySlotContents(slot, itemStack.copy());
        }
        else if (this.getStackInSlot(slot).isItemEqual(itemStack))
        {
            this.getStackInSlot(slot).stackSize++;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
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
        this.fangXiang = nbt.getByte("fangXiang");

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
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
        nbt.setByte("fangXiang", this.fangXiang);

    }

    @Override
    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(this.fangXiang);
    }

    @Override
    public void setDirection(ForgeDirection facingDirection)
    {
        this.fangXiang = (byte) facingDirection.ordinal();
    }

    @Override
    public int getSizeInventory()
    {
        return 2;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return false;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, Reference.PREFIX + "interface", 1, (float) (this.worldObj.rand.nextFloat() * 0.2 + 0.9F));
        if(!this.worldObj.isRemote)
        	entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        // TODO Auto-generated method stub

    }
}
