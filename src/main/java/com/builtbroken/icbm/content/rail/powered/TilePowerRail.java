package com.builtbroken.icbm.content.rail.powered;

import com.builtbroken.icbm.content.rail.IMissileRail;
import com.builtbroken.icbm.content.rail.entity.EntityCart;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileEnt;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

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
        if (type == 1)
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
        else if (type == 0)
        {
            handlePush(cart);
        }
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
        final double vel = 0.3;
        cart.recenterCartOnRail(getAttachedDirection(), getFacingDirection(), getRailHeight());
        switch (getAttachedDirection())
        {
            case UP:
                switch (getFacingDirection())
                {
                    case NORTH:
                    case SOUTH:
                    case EAST:
                    case WEST:
                }
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeInt(getFacingDirection().ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("facingDirection"))
        {
            setFacingDirection(ForgeDirection.getOrientation(nbt.getInteger("facingDirection")));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("facingDirection", getFacingDirection().ordinal());
    }

    @Override
    public Cube getCollisionBounds()
    {
        if(world() != null)
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
                    return new Cube(0, 0, 0, .4, 1, 1);
                case WEST:
                    return COLLISION_BOX_WEST;
            }
        }
        return bounds;
    }
}
