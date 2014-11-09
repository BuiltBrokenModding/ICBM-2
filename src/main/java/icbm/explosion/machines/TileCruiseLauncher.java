package icbm.explosion.machines;

import icbm.core.ICBMCore;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.ex.Explosion;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.items.ItemMissile;
import icbm.explosion.machines.launcher.TileLauncherPrefab;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import resonant.api.explosion.ExplosiveType;
import resonant.api.explosion.ILauncherContainer;
import resonant.api.explosion.ILauncherController;
import resonant.api.explosion.IMissile;
import resonant.api.explosion.LauncherType;
import resonant.api.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import resonant.lib.multiblock.IBlockActivate;
import resonant.lib.network.IPacketReceiver;
import resonant.lib.utility.LanguageUtility;
import resonant.api.energetic.EnergyStorage;
import resonant.lib.transform.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;

import dan200.computercraft.api.peripheral.IPeripheral;

public class TileCruiseLauncher extends TileLauncherPrefab implements IBlockActivate, IInventory, ILauncherContainer, IPacketReceiver
{
    // The missile that this launcher is holding
    public IMissile daoDan = null;

    public float rotationYaw = 0;

    public float rotationPitch = 0;

    /** The ItemStacks that hold the items currently being used in the missileLauncher */
    private ItemStack[] containingItems = new ItemStack[2];

    public TileCruiseLauncher()
    {
        super();
        this.targetPos = new Vector3();
        setEnergyHandler(new EnergyStorage(100000000));
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

    /** Gets the display status of the missile launcher
     * 
     * @return The string to be displayed */
    @Override
    public String getStatus()
    {
        String color = "\u00a74";
        String status = LanguageUtility.getLocal("gui.misc.idle");

        if (!this.getEnergyHandler().isFull())
        {
            status = LanguageUtility.getLocal("gui.launcherCruise.statusNoPower");
        }
        else if (this.daoDan == null)
        {
            status = LanguageUtility.getLocal("gui.launcherCruise.statusEmpty");
        }
        else if (this.targetPos == null)
        {
            status = LanguageUtility.getLocal("gui.launcherCruise.statusInvalid");
        }
        else
        {
            color = "\u00a72";
            status = LanguageUtility.getLocal("gui.launcherCruise.statusReady");
        }

        return color + status;
    }

    /** Returns the name of the inventory. */
    @Override
    public String getInvName()
    {
        return LanguageUtility.getLocal("gui.launcherCruise.name");
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        this.discharge(this.containingItems[1]);

        // Rotate the yaw
        if (this.getYawFromTarget() - this.rotationYaw != 0)
        {
            this.rotationYaw += (this.getYawFromTarget() - this.rotationYaw) * 0.1;
        }
        if (this.getPitchFromTarget() - this.rotationPitch != 0)
        {
            this.rotationPitch += (this.getPitchFromTarget() - this.rotationPitch) * 0.1;
        }

        if (!this.worldObj.isRemote)
        {
            this.setMissile();

            if (this.ticks % 100 == 0 && this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
            {
                this.launch();
            }
        }
    }

    @Override
    public void placeMissile(ItemStack itemStack)
    {
        this.containingItems[0] = itemStack;
    }

    public void setMissile()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.containingItems[0] != null)
            {
                if (this.containingItems[0].getItem() instanceof ItemMissile)
                {
                    int haoMa = this.containingItems[0].getItemDamage();
                    if (ExplosiveRegistry.get(haoMa) instanceof Explosion)
                    {
                        Explosion missile = (Explosion) ExplosiveRegistry.get(haoMa);

                        ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ExplosiveType.AIR, missile);
                        MinecraftForge.EVENT_BUS.post(evt);

                        if (!evt.isCanceled())
                        {
                            if (this.daoDan == null)
                            {

                                if (missile.isCruise() && missile.getTier() <= 3)
                                {
                                    Vector3 startingPosition = new Vector3((this.xCoord + 0.5f), (this.yCoord + 1f), (this.zCoord + 0.5f));
                                    this.daoDan = new EntityMissile(this.worldObj, startingPosition, new Vector3(this), haoMa);
                                    this.worldObj.spawnEntityInWorld((Entity) this.daoDan);
                                    return;
                                }
                            }

                            if (this.daoDan != null)
                            {
                                if (this.daoDan.getExplosiveType().getID() == haoMa)
                                {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (this.daoDan != null)
            {
                ((Entity) this.daoDan).setDead();
            }

            this.daoDan = null;
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, 0, this.getEnergyHandler().getEnergy(), this.getFrequency(), this.targetPos.intX(), this.targetPos.intY(), this.targetPos.intZ());
    }

    @Override
    protected void onReceivePacket(int id, ByteArrayDataInput data, EntityPlayer player, Object... extra) throws IOException
    {
        super.onReceivePacket(id, data, player, extra);

        try
        {
            switch (id)
            {
                case 0:
                {
                    this.getEnergyHandler().setEnergy(data.readLong());
                    this.setFrequency(data.readInt());
                    this.targetPos = new Vector3(data.readInt(), data.readInt(), data.readInt());
                    break;
                }
                case 1:
                {
                    this.setFrequency(data.readInt());
                    break;
                }
                case 2:
                {
                    this.targetPos = new Vector3(data.readInt(), data.readInt(), data.readInt());
                    break;

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private float getPitchFromTarget()
    {
        double distance = Math.sqrt((this.targetPos.x - this.xCoord) * (this.targetPos.x - this.xCoord) + (this.targetPos.z - this.zCoord) * (this.targetPos.z - this.zCoord));
        return (float) Math.toDegrees(Math.atan((this.targetPos.y - (this.yCoord + 0.5F)) / distance));
    }

    private float getYawFromTarget()
    {
        double xDifference = this.targetPos.x - (this.xCoord + 0.5F);
        double yDifference = this.targetPos.z - (this.zCoord + 0.5F);
        return (float) Math.toDegrees(Math.atan2(yDifference, xDifference));
    }

    @Override
    public boolean canLaunch()
    {
        if (this.daoDan != null && this.containingItems[0] != null)
        {
            Explosion missile = (Explosion) ExplosiveRegistry.get(this.containingItems[0].getItemDamage());

            if (missile != null && missile.getID() == daoDan.getExplosiveType().getID() && missile.isCruise() && missile.getTier() <= 3)
            {
                if (this.getEnergyHandler().isFull())
                {
                    if (!this.isTooClose(this.targetPos))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /** Launches the missile
     * 
     * @param targetVector - The target in which the missile will land in */
    @Override
    public void launch()
    {
        if (this.canLaunch())
        {
            this.decrStackSize(0, 1);
            this.setEnergy(ForgeDirection.UNKNOWN, 0);
            this.daoDan.launch(this.targetPos);
            this.daoDan = null;
        }
    }

    // Is the target too close?
    public boolean isTooClose(Vector3 target)
    {
        return Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < 8;
    }

    /** Reads a tile entity from NBT. */
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
    }

    /** Writes a tile entity to NBT. */
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
        
        if(!this.worldObj.isRemote)
        	player.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public LauncherType getLauncherType()
    {
        return LauncherType.CRUISE;
    }

    @Override
    public boolean isInvNameLocalized()
    {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemStack)
    {
        if (itemStack != null)
        {
            if (itemStack.getItem() instanceof ItemMissile && this.getStackInSlot(slotID) == null)
            {
                if (ExplosiveRegistry.get(itemStack.getItemDamage()) instanceof Explosion)
                {
                    Explosion missile = (Explosion) ExplosiveRegistry.get(itemStack.getItemDamage());

                    if (missile.isCruise() && missile.getTier() <= 3)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void setContainingMissile(IMissile missile)
    {
        this.daoDan = missile;
    }

    @Override
    public ILauncherController getController()
    {
        return this;
    }

    @Override
    public IMissile getMissile()
    {
        return this.daoDan;
    }

    @Override
    public IMissile getContainingMissile()
    {
        return this.daoDan;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 1, yCoord + 1, zCoord + 1);
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
    public int[] getMissileSlots()
    {
        return new int[] { 0 };
    }

    @Override
    public boolean equals(IPeripheral other)
    {
        return equals(other);
    }
}
