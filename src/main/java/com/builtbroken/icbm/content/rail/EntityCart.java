package com.builtbroken.icbm.content.rail;

import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.entity.EntityBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2016.
 */
public class EntityCart extends EntityBase
{
    //Thing we are carrying
    private Missile cargo;

    /** Side of the cart that the rail exists */
    public ForgeDirection railSide;

    public EntityCart(World world)
    {
        super(world);
        this.setSize(0.5f, 0.5f);
    }

    @Override
    public void onUpdate()
    {
        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

        //Grab rail side if null
        if (railSide == null)
        {
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
            {
                Pos pos = new Pos(this).add(side);
                Block block = pos.getBlock(worldObj);
                final TileEntity tile = pos.getTileEntity(worldObj);
                if (!(block instanceof BlockRail || tile instanceof IMissileRail))
                {
                    railSide = side;
                    break;
                }
            }
        }

        if (!worldObj.isRemote)
        {
            final Pos pos = new Pos(this).add(railSide);
            final Block block = pos.getBlock(worldObj);
            final TileEntity tile = pos.getTileEntity(worldObj);
            //Kills entity if it falls out of the world, should never happen
            if (this.posY < -64.0D)
            {
                this.kill();
            }
            //Breaks entity if its not on a track
            else if (!(block instanceof BlockRail || tile instanceof IMissileRail))
            {
                destroyCart();
            }
            else if (tile instanceof IMissileRail)
            {
                ((IMissileRail) tile).tickRailFromCart(this);
                recenterCartOnRail(((IMissileRail) tile).getAttachedDirection(), ((IMissileRail) tile).getFacingDirection(), 0.4);
            }
            else
            {
                final int meta = pos.getBlockMetadata(worldObj);
                BlockRail.RailDirections railType = BlockRail.RailDirections.get(meta);
                recenterCartOnRail(railType.side, railType.facing, block.getBlockBoundsMaxY());
            }
        }
        doCollisionLogic();
    }

    /**
     * Clamps the movement of the cart and position data so the
     * cart stays on the rail.
     *
     * @param side       - side the rail is attached to
     * @param facing     - direction the movement is going into
     * @param railHeight - height of the rail the cart is on
     */
    public void recenterCartOnRail(ForgeDirection side, ForgeDirection facing, double railHeight)
    {
        if (side == ForgeDirection.UP)
        {
            if (facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH)
            {
                motionX = 0;
                motionY = 0;
                posY = ((int) posY) + railHeight;
            }
            else if (facing == ForgeDirection.EAST || facing == ForgeDirection.WEST)
            {
                motionZ = 0;
                motionY = 0;
                posY = ((int) posY) + railHeight;
            }
        }
        //TODO fix as this will not work with the current rail logic
        else if (side == ForgeDirection.DOWN)
        {
            if (facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH)
            {
                motionX = 0;
                motionY = 0;
                posY = ((int) posY) + 1 - railHeight;
            }
            else if (facing == ForgeDirection.EAST || facing == ForgeDirection.WEST)
            {
                motionZ = 0;
                motionY = 0;
                posY = ((int) posY) + 1 - railHeight;
            }
        }
    }

    protected void doCollisionLogic()
    {
        AxisAlignedBB box = boundingBox.expand(0.2D, 0.0D, 0.2D);

        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, box);

        if (list != null && !list.isEmpty())
        {
            for (Object aList : list)
            {
                Entity entity = (Entity) aList;

                if (entity != this.riddenByEntity && entity.canBePushed())
                {
                    entity.applyEntityCollision(this);
                }
            }
        }
    }

    @Override
    public void applyEntityCollision(Entity p_70108_1_)
    {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0;
    }

    public void destroyCart()
    {
        //TODO drop item
        setDead();
    }

    @Override
    public void moveEntity(double byX, double byY, double byZ)
    {
        this.worldObj.theProfiler.startSection("move");
        this.ySize *= 0.4F;

        //Store previous movement values
        final double prevMoveX = byX;
        final double prevMoveY = byY;
        final double prevMoveZ = byZ;

        //Get collision boxes
        List collisionBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(byX, byY, byZ));

        //Calculate offset for y collision
        for (Object collisionBoxe : collisionBoxes)
        {
            byY = ((AxisAlignedBB) collisionBoxe).calculateYOffset(this.boundingBox, byY);
        }
        this.boundingBox.offset(0.0D, byY, 0.0D);

        //Calculate offset for x collision
        for (Object collisionBoxe : collisionBoxes)
        {
            byX = ((AxisAlignedBB) collisionBoxe).calculateXOffset(this.boundingBox, byX);
        }
        this.boundingBox.offset(byX, 0.0D, 0.0D);

        //Calculate offset for z collision
        for (Object collisionBoxe : collisionBoxes)
        {
            byZ = ((AxisAlignedBB) collisionBoxe).calculateZOffset(this.boundingBox, byZ);
        }
        this.boundingBox.offset(0.0D, 0.0D, byZ);

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rest");

        this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
        this.posY = this.boundingBox.minY + (double) this.yOffset - (double) this.ySize;
        this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;

        this.isCollidedHorizontally = prevMoveX != byX || prevMoveZ != byZ;
        this.isCollidedVertically = prevMoveY != byY;

        this.onGround = prevMoveY != byY && prevMoveY < 0.0D;
        this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;

        if (prevMoveX != byX)
        {
            this.motionX = 0.0D;
        }

        if (prevMoveY != byY)
        {
            this.motionY = 0.0D;
        }

        if (prevMoveZ != byZ)
        {
            this.motionZ = 0.0D;
        }

        this.worldObj.theProfiler.endSection();
    }

    @Override
    public void onEntityUpdate()
    {
        //Empty to ignore default entity code
    }
}
