package icbm.content.tile.assembler;

import icbm.ICBM;
import icbm.content.entity.EntityMissile;
import icbm.content.items.ItemMissile;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.IRotatable;
import resonant.api.ITier;
import resonant.lib.content.prefab.java.TileInventory;
import resonant.lib.multiblock.reference.IMultiBlock;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;
import resonant.lib.utility.LanguageUtility;

import java.util.ArrayList;
import java.util.List;

/** Basic display board for Missiles
 * TODO implement redstone pulse to dummy fire rocket
 * @author Darkguardsman
 */
public class TileMissileAssembler extends TileInventory implements IMultiBlock, ITier, IRotatable, IPacketReceiver
{
    public int tier = -1, missileID = -1;
    /**
     * Side placed on
     */
    public ForgeDirection placedSide = ForgeDirection.UP;
    /**
     * 0 - 3 of rotation on the given side
     */
    public byte rotationSide = 0;
    public boolean rotating = false;

    EntityMissile missile;
    private ItemStack[] containingItems = new ItemStack[1];

    public TileMissileAssembler()
    {
        super(Material.iron);
        setSizeInventory(1);
    }

    @Override
    public void start()
    {
        super.start();
        this.onInventoryChanged();
    }

    @Override
    public List<Vector3> getMultiBlockVectors()
    {
        return getMultiBlockVectors(placedSide, rotationSide);
    }

    public static List<Vector3> getMultiBlockVectors(ForgeDirection side, byte rot)
    {
        List<Vector3> list = new ArrayList<Vector3>();

        // rotation doesn't really effect the multi block too much however placed side does
        if (side == ForgeDirection.UP || side == ForgeDirection.DOWN)
        {
            // line up on the x
            if (rot == 0 || rot == 2)
            {
                list.add(new Vector3(1, 0, 0));
                list.add(new Vector3(-1, 0, 0));

            } else // lined up on the z
            {
                list.add(new Vector3(0, 0, 1));
                list.add(new Vector3(0, 0, -1));
            }
            return list;
        } else
        {
            // Lined up with x or z
            if (rot == 0 || rot == 2)
            {
                if (side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH)
                {
                    list.add(new Vector3(-1, 0, 0));
                    list.add(new Vector3(1, 0, 0));
                } else if (side == ForgeDirection.EAST || side == ForgeDirection.WEST)
                {
                    list.add(new Vector3(0, 0, -1));
                    list.add(new Vector3(0, 0, 1));
                }
                return list;
            }
            // Lined up with the Y
            list.add(new Vector3(0, 1, 0));
            list.add(new Vector3(0, -1, 0));
            return list;
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
        } else if (this.rotationSide == 2)
        {
            return ForgeDirection.SOUTH;
        } else if (this.rotationSide == 1)
        {
            return ForgeDirection.EAST;
        } else
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
        } else if (direction == ForgeDirection.SOUTH)
        {
            rot = 2;
        } else if (direction == ForgeDirection.EAST)
        {
            rot = 1;
        } else
        {
            rot = 3;
        }
        if (canPlaceBlockAt(this.placedSide, rot))
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
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("rotationSide", this.rotationSide);
        nbt.setByte("placedSide", (byte) this.placedSide.ordinal());
    }

    @Override
    public boolean use(EntityPlayer entityPlayer, int side, Vector3 hit)
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

        if (!this.worldObj.isRemote)
            entityPlayer.openGui(ICBM.INSTANCE, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType type)
    {
        if (this.worldObj.isRemote)
        {
            byte id = data.readByte();

            if (id == 0)
            {
                this.rotationSide = data.readByte();
                this.placedSide = ForgeDirection.getOrientation(data.readByte());
                this.missileID = data.readInt();
                this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, ((byte) 0), this.rotationSide, ((byte) this.placedSide.ordinal()), this.missileID);
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
            } else
            {
                missileID = -1;
            }
        }
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return this.containingItems.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int par1)
    {
        return this.containingItems[par1];
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg.
     * Returns the new stack.
     */
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
            } else
            {
                var3 = this.containingItems[par1].splitStack(par2);

                if (this.containingItems[par1].stackSize == 0)
                {
                    this.containingItems[par1] = null;
                }

                return var3;
            }
        } else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as
     * an EntityItem - like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.containingItems[par1] != null)
        {
            ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        } else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor
     * sections).
     */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.containingItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getInventoryName()
    {
        return LanguageUtility.getLocal("gui.assembler.name");
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemStack)
    {
        return itemStack.getItem() instanceof ItemMissile;
    }

    @Override
    public boolean configure(EntityPlayer entityPlayer, int s, Vector3 hit)
    {
        if (world().isRemote)
        {
            return true;
        }
        byte rotation = rotationSide;
        ForgeDirection side = placedSide;
        if (rotation == 3)
        {
            rotation = 0;
        } else
        {
            rotation++;
        }
        if (canPlaceBlockAt(side, rotation))
        {
           //TODO re-implement wrench rotation
            return true;

        }

        return false;
    }

    public boolean canPlaceBlockAt(ForgeDirection placeSide, int rot)
    {
        VectorWorld pos = toVectorWorld();
        if (pos.isAirBlock() || pos.isBlockEqual(ICBM.blockMissileAssembler))
        {
            List<Vector3> vecs = TileMissileAssembler.getMultiBlockVectors(placeSide, (byte) rot);

            for (Vector3 v : vecs)
            {
                VectorWorld vec = pos.clone().add(v);
                if (!vec.isAirBlock())
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
