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

    /** Side of the block we are attached to */
    private ForgeDirection attachedSide;

    /** Direction we are facing */
    protected ForgeDirection facingDirection = ForgeDirection.NORTH;


    public TilePowerRail()
    {
        super("cartRotator", Material.iron);
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
            handlePush(cart, facingDirection);
        }
    }

    @Override
    public ForgeDirection getAttachedDirection()
    {
        if(attachedSide == null)
        {
            attachedSide = ForgeDirection.getOrientation(getMetadata());
        }
        return attachedSide;
    }

    @Override
    public ForgeDirection getFacingDirection()
    {
        return facingDirection;
    }

    @Override
    public double railHeight()
    {
        return 0.4;
    }

    protected void handlePush(EntityCart cart, ForgeDirection pushDirection)
    {
        final double vel = 0.3;

    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeInt(facingDirection.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("facingDirection"))
        {
            facingDirection = ForgeDirection.getOrientation(nbt.getInteger("facingDirection"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("facingDirection", facingDirection.ordinal());
    }
}
