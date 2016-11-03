package com.builtbroken.icbm.content.rail.powered;

import com.builtbroken.icbm.content.rail.IMissileRail;
import com.builtbroken.icbm.content.rail.entity.EntityCart;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileEnt;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Handles different functions
 * <p>
 * A) Handles rotation of the block on any axis.
 * <p>
 * B) Handles pushing the carts forward
 * <p>
 * B is the lower tech version of A only pushing the carts and can not actually move the carts. While A can also push and rotate
 * the carts allowing it to be used in place of a B type rail.
 * <p>
 * C) handles directing the cart from one direction
 * into another direction. So long as the direction does not change facing side of the block.
 * Example North side moving into the block, redirect upwards, still on the north side of the block but moving up instead of forwards.
 * <p>
 * D) stops the cart from moving, should be redstone controllable with no redstone being off by default
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2016.
 */
public class TilePowerRail extends TileEnt implements IMissileRail, IPacketIDReceiver
{
    //TODO A type rails need to have a rotation symbol to show the direction the cart is rotated towards
    //TODO B type rails need to have an arrow showing the push direction
    //TODO C type rails need to show a stair case symbol to show it moving up or down
    //TODO D type needs to have a redstone upgrade
    //TODO D type needs to show when its power and if it will stop a cart (Use gate symbol | | open /\ closed)
    protected int type = 0;
    /** How much to rotate */
    protected int rotateYaw = 90;
    /** Are we rotating to an angle or just rotating by an angle */
    protected boolean rotateToAngle = true;
    /** Are we rotating clockwise, not used if setting angle */
    protected boolean rotateClockwise = true;

    /** Side of the block we are attached to */
    private ForgeDirection attachedSide;

    private ForgeDirection facingDirection = ForgeDirection.NORTH;

    //Collision/Render/Selection boxes
    private static final Cube COLLISION_BOX_DOWN = new Cube(0, .6, 0, 1, 1, 1);

    private static final Cube COLLISION_BOX_NORTH = new Cube(0, 0, .6, 1, 1, 1);
    private static final Cube COLLISION_BOX_SOUTH = new Cube(0, 0, 0, 1, 1, .4);

    private static final Cube COLLISION_BOX_EAST = new Cube(0, 0, 0, .4, 1, 1);
    private static final Cube COLLISION_BOX_WEST = new Cube(.6, 0, 0, 1, 1, 1);

    public TilePowerRail()
    {
        super("cartPowerRail", Material.iron);
        this.bounds = new Cube(0, 0, 0, 1, .4, 1);
        this.itemBlock = ItemBlockPowerRail.class;
    }

    @Override
    public Tile newTile()
    {
        return new TilePowerRail();
    }

    @Override
    public void tickRailFromCart(EntityCart cart)
    {
        if (isRotationRail())
        {
            //TODO lerp rotation to provide a transition
            if (rotateToAngle)
            {
                cart.rotationYaw = rotateYaw;
            }
            else if (rotateClockwise)
            {
                cart.rotationYaw += rotateYaw;
            }
            else
            {
                cart.rotationYaw -= rotateYaw;
            }
            handlePush(cart);
        }
        else if (isPoweredRail())
        {
            handlePush(cart);
        }
        else if (isOrientationRail())
        {

        }
        else if (isStopRail())
        {

        }
    }

    public boolean isPoweredRail()
    {
        return type == 1;
    }

    public boolean isRotationRail()
    {
        return type == 0;
    }

    public boolean isOrientationRail()
    {
        return type == 2;
    }

    public boolean isStopRail()
    {
        return type == 3;
    }

    @Override
    public ForgeDirection getAttachedDirection()
    {
        if (attachedSide == null)
        {
            attachedSide = ForgeDirection.getOrientation(getMetadata());
        }
        return attachedSide;
    }

    public void setFacingDirection(ForgeDirection facingDirection)
    {
        this.facingDirection = facingDirection;
        if (world() != null && isServer())
        {
            sendDescPacket();
        }
    }

    /** Direction we are facing */
    @Override
    public ForgeDirection getFacingDirection()
    {
        return facingDirection;
    }

    @Override
    public double getRailHeight()
    {
        return 0.4;
    }

    protected void handlePush(EntityCart cart)
    {
        if (isServer())
        {
            final double vel = 0.3;
            cart.recenterCartOnRail(getAttachedDirection(), getFacingDirection(), getRailHeight());

            //Cancel existing motion
            cart.motionX = 0;
            cart.motionY = 0;
            cart.motionZ = 0;

            switch (getAttachedDirection())
            {
                case UP:
                    switch (getFacingDirection())
                    {
                        case NORTH:
                            cart.motionZ = -vel;
                            break;
                        case SOUTH:
                            cart.motionZ = vel;
                            break;
                        case EAST:
                            cart.motionX = vel;
                            break;
                        case WEST:
                            cart.motionX = -vel;
                            break;
                    }
            }
        }
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (Engine.runningAsDev)
        {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.stick)
            {
                if (isServer())
                {
                    player.addChatMessage(new ChatComponentText("A: " + getAttachedDirection() + " F:" + getFacingDirection() + " T:" + type));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if(player.isSneaking())
        {
            if(isRotationRail())
            {
                rotateClockwise = !rotateClockwise;
                if(isServer())
                {
                    sendDescPacket();
                }
                else
                {
                    world().markBlockRangeForRenderUpdate(xi(), yi(), zi(), xi(), yi(), zi());
                }
            }
        }
        else
        {
            if (side == 0 || side == 1)
            {
                boolean high = hit.z() >= 0.7;
                boolean low = hit.z() <= 0.3;
                //Left & right are inverted for South
                boolean left = hit.x() <= 0.3;
                boolean right = hit.x() >= 0.7;

                if (!left && !right)
                {
                    if (high)
                    {
                        setFacingDirection(ForgeDirection.SOUTH);
                    }
                    else if (low)
                    {
                        setFacingDirection(ForgeDirection.NORTH);
                    }
                }
                else
                {
                    if (left)
                    {
                        setFacingDirection(side == 0 ? ForgeDirection.EAST : ForgeDirection.WEST);
                    }
                    else if (right)
                    {
                        setFacingDirection(side == 0 ? ForgeDirection.WEST : ForgeDirection.EAST);
                    }
                }
            }
            //North South
            else if (side == 2 || side == 3)
            {
                boolean high = hit.y() >= 0.7;
                boolean low = hit.y() <= 0.3;
                //Left & right are inverted for South
                boolean left = hit.x() <= 0.3;
                boolean right = hit.x() >= 0.7;

                if (!left && !right)
                {
                    if (high)
                    {
                        setFacingDirection(ForgeDirection.UP);
                    }
                    else if (low)
                    {
                        setFacingDirection(ForgeDirection.DOWN);
                    }
                }
                else
                {
                    if (left)
                    {
                        setFacingDirection(ForgeDirection.WEST);
                    }
                    else if (right)
                    {
                        setFacingDirection(ForgeDirection.EAST);
                    }
                }
            }
            //West East
            else if (side == 4 || side == 5)
            {
                boolean high = hit.y() >= 0.7;
                boolean low = hit.y() <= 0.3;
                //Left & right are inverted for East
                boolean left = hit.z() <= 0.3;
                boolean right = hit.z() >= 0.7;

                if (!left && !right)
                {
                    if (high)
                    {
                        setFacingDirection(ForgeDirection.UP);
                    }
                    else if (low)
                    {
                       setFacingDirection(ForgeDirection.DOWN);
                    }
                }
                else
                {
                    if (left)
                    {
                        setFacingDirection(ForgeDirection.NORTH);
                    }
                    else if (right)
                    {
                        setFacingDirection(ForgeDirection.SOUTH);
                    }
                }
            }
            world().markBlockRangeForRenderUpdate(xi(), yi(), zi(), xi(), yi(), zi());
        }
        return true;
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeInt(type);
        buf.writeInt(getFacingDirection().ordinal());
        if (isRotationRail())
        {
            buf.writeBoolean(rotateToAngle);
            buf.writeBoolean(rotateClockwise);
            buf.writeInt(rotateYaw);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("facingDirection"))
        {
            setFacingDirection(ForgeDirection.getOrientation(nbt.getInteger("facingDirection")));
        }
        type = nbt.getInteger("railType");
        if (isRotationRail())
        {
            if (nbt.hasKey("rotateToAngle"))
            {
                rotateToAngle = nbt.getBoolean("rotateToAngle");
            }
            if (nbt.hasKey("rotateClockwise"))
            {
                rotateClockwise = nbt.getBoolean("rotateClockwise");
            }
            if (nbt.hasKey("rotationYaw"))
            {
                rotateYaw = nbt.getInteger("rotationYaw");
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("facingDirection", getFacingDirection().ordinal());
        nbt.setInteger("railType", type);
        if (isRotationRail())
        {
            nbt.setBoolean("rotateToAngle", rotateToAngle);
            nbt.setBoolean("rotateClockwise", rotateClockwise);
            nbt.setInteger("rotationYaw", rotateYaw);
        }
    }

    @Override
    public Cube getCollisionBounds()
    {
        if (world() != null)
        {
            switch (ForgeDirection.getOrientation(world().getBlockMetadata(xi(), yi(), zi())))
            {
                case DOWN:
                    return COLLISION_BOX_DOWN;
                case NORTH:
                    return COLLISION_BOX_NORTH;
                case SOUTH:
                    return COLLISION_BOX_SOUTH;
                case EAST:
                    return COLLISION_BOX_EAST;
                case WEST:
                    return COLLISION_BOX_WEST;
            }
        }
        return bounds;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
    {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
    }
}
