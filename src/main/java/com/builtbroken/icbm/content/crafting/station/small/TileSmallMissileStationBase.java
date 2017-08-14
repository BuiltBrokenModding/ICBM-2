package com.builtbroken.icbm.content.crafting.station.small;

import com.builtbroken.icbm.content.crafting.station.TileAbstractWorkstation;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.framework.multiblock.EnumMultiblock;
import com.builtbroken.mc.framework.multiblock.MultiBlockHelper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/11/2016.
 */
public abstract class TileSmallMissileStationBase<I extends IInventory> extends TileAbstractWorkstation<I>
{
    //Static values
    public static final int INPUT_SLOT = 0;

    /** Multi-block set for up-down row */
    public static HashMap<IPos3D, String> upDownMap;
    /** Multi-block set for east-west row */
    public static HashMap<IPos3D, String> eastWestMap;
    /** Multi-block set for north-south row */
    public static HashMap<IPos3D, String> northSouthMap;

    static
    {
        //Only 3 actual multi-block sets used 4 times each to create the 12 different rotation cases
        upDownMap = new HashMap();
        upDownMap.put(new Pos(0, 1, 0), EnumMultiblock.INVENTORY.getTileName() + "#RenderBlock=false");
        upDownMap.put(new Pos(0, -1, 0), EnumMultiblock.INVENTORY.getTileName() + "#RenderBlock=false");

        eastWestMap = new HashMap();
        eastWestMap.put(new Pos(1, 0, 0), EnumMultiblock.INVENTORY.getTileName() + "#RenderBlock=false");
        eastWestMap.put(new Pos(-1, 0, 0), EnumMultiblock.INVENTORY.getTileName() + "#RenderBlock=false");

        northSouthMap = new HashMap();
        northSouthMap.put(new Pos(0, 0, 1), EnumMultiblock.INVENTORY.getTileName() + "#RenderBlock=false");
        northSouthMap.put(new Pos(0, 0, -1), EnumMultiblock.INVENTORY.getTileName() + "#RenderBlock=false");
    }

    public TileSmallMissileStationBase(String name, Material material)
    {
        super(name, material);
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        this.connectedBlockSide = ForgeDirection.getOrientation(oldWorld().getBlockMetadata(xi(), yi(), zi()));
        //Force rotation update if it is invalid or blocked
        if (!isRotationValid() || isRotationBlocked(facing))
        {
            this.facing = getDirection();
        }
        else
        {
            MultiBlockHelper.buildMultiBlock(oldWorld(), this, true, true);
        }
        oldWorld().markBlockForUpdate(xi(), yi(), zi());
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            if (player.isSneaking())
            {
                //Simple rotation so not extra checks are needed
                player.addChatComponentMessage(new ChatComponentText("Inverted station rotation"));
                setDirection(getDirection().getOpposite());
            }
            else
            {
                ForgeDirection newDir = getNextRotation();
                if (newDir == ForgeDirection.UNKNOWN)
                {
                    player.addChatComponentMessage(new ChatComponentText("Error connect side is not set, remove and replace block"));
                }
                else if (!isRotationBlocked(newDir))
                {
                    setDirection(newDir);
                    player.addChatComponentMessage(new ChatComponentText("Rotated to face set to " + getDirection()));
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText("Can't rotate from " + getDirection() + " to " + newDir + " as there are blocks in the way"));
                }
            }
        }
        return true;
    }

    /**
     * Gets the next rotation around the connect side
     *
     * @return next rotation, or UNKNOWN if invalid
     */
    public ForgeDirection getNextRotation()
    {
        ForgeDirection newDir = ForgeDirection.UNKNOWN;
        switch (this.connectedBlockSide)
        {
            case UP:
            case DOWN:
                if (facing == ForgeDirection.NORTH)
                {
                    newDir = ForgeDirection.EAST;
                }
                else if (facing == ForgeDirection.EAST)
                {
                    newDir = ForgeDirection.SOUTH;
                }
                else if (facing == ForgeDirection.SOUTH)
                {
                    newDir = ForgeDirection.WEST;
                }
                else
                {
                    newDir = ForgeDirection.NORTH;
                }
                break;
            case EAST:
            case WEST:
                if (facing == ForgeDirection.NORTH)
                {
                    newDir = ForgeDirection.DOWN;
                }
                else if (facing == ForgeDirection.DOWN)
                {
                    newDir = ForgeDirection.SOUTH;
                }
                else if (facing == ForgeDirection.SOUTH)
                {
                    newDir = ForgeDirection.UP;
                }
                else
                {
                    newDir = ForgeDirection.NORTH;
                }
                break;
            case NORTH:
            case SOUTH:
                if (facing == ForgeDirection.EAST)
                {
                    newDir = ForgeDirection.DOWN;
                }
                else if (facing == ForgeDirection.DOWN)
                {
                    newDir = ForgeDirection.WEST;
                }
                else if (facing == ForgeDirection.WEST)
                {
                    newDir = ForgeDirection.UP;
                }
                else
                {
                    newDir = ForgeDirection.EAST;
                }
                break;
        }
        return newDir;
    }

    @Override
    public void setDirection(ForgeDirection newDir)
    {
        setDirectionDO(newDir, isServer());
    }

    @Override
    public ForgeDirection getDirection()
    {
        //Fixed invalid rotations
        if (!isValidRotation(facing))
        {
            for (int i = 0; i < 5; i++)
            {
                if (setDirectionDO(getNextRotation(), isServer()))
                {
                    return facing;
                }
            }
            InventoryUtility.dropBlockAsItem(oldWorld(), xi(), yi(), zi(), true);
        }
        return facing;
    }


    /**
     * Seperate version of {@link TileSmallMissileWorkstation#setDirection(ForgeDirection)} that returns true if it rotated.
     * <p>
     * This is mainly for testing threw JUnit, meaning don't call this method directly unless you need more control.
     *
     * @param newDir - direction to change to
     * @return false if it didn't rotate
     */
    public boolean setDirectionDO(ForgeDirection newDir, boolean sendPacket)
    {
        //Rotation can't equal the connected side or it's opposite as this will place it into a wall
        if (facing != newDir && newDir != connectedBlockSide && newDir != connectedBlockSide.getOpposite())
        {
            //Only run placement and structure checks if rotated by 90 degrees
            if (newDir != facing.getOpposite())
            {
                if (!isRotationBlocked(newDir))
                {
                    //Clear and rebuild multi block
                    rotating = true;
                    breakDownStructure(false, false);
                    //Change rotation after breaking down the structure and before making the new structure
                    facing = newDir;
                    MultiBlockHelper.buildMultiBlock(oldWorld(), this, true, true);
                    MultiBlockHelper.updateStructure(oldWorld(), this, true);
                    rotating = false;
                }
                else
                {
                    //Failed as there are no free blocks to move to
                    return false;
                }
            }
            else
            {
                facing = newDir;
            }
            if (sendPacket)
            {
                sendDescPacket();
            }
            return true;
        }
        return false;
    }


    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock(ForgeDirection dir)
    {
        return getLayoutOfMultiBlock(dir, connectedBlockSide);
    }

    /**
     * Grabs the layout of the structure for the given rotation and side
     *
     * @param dir
     * @param connectedBlockSide
     * @return
     */
    public static HashMap<IPos3D, String> getLayoutOfMultiBlock(ForgeDirection dir, ForgeDirection connectedBlockSide)
    {
        switch (connectedBlockSide)
        {
            case UP:
            case DOWN:
                if (dir == ForgeDirection.EAST || dir == ForgeDirection.WEST)
                {
                    return eastWestMap;
                }
                else
                {
                    return northSouthMap;
                }
            case EAST:
            case WEST:
                if (dir == ForgeDirection.DOWN || dir == ForgeDirection.UP)
                {
                    return upDownMap;
                }
                else
                {
                    return northSouthMap;
                }
            case NORTH:
            case SOUTH:
                if (dir == ForgeDirection.DOWN || dir == ForgeDirection.UP)
                {
                    return upDownMap;
                }
                else
                {
                    return eastWestMap;
                }
        }
        return eastWestMap;
    }

    /**
     * Checks if the current rotation value is valid for the connected side
     *
     * @return true if it is valid
     */
    public boolean isRotationValid()
    {
        return isValidRotation(facing);
    }

    /**
     * Checks if a new rotation is valid
     *
     * @param dir - new rotation
     * @return true if the rotation is valid
     */
    public boolean isValidRotation(ForgeDirection dir)
    {
        return dir != ForgeDirection.UNKNOWN && dir != connectedBlockSide && dir != connectedBlockSide.getOpposite();
    }

    protected void reducePlayerHeldItem(EntityPlayer player)
    {
        player.getHeldItem().stackSize--;
        if (player.getHeldItem().stackSize <= 0)
        {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
        player.inventoryContainer.detectAndSendChanges();
    }
}
