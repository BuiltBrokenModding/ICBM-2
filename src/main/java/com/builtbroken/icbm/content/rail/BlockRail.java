package com.builtbroken.icbm.content.rail;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * A single line rail used to move missiles around automatically
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2016.
 */
public class BlockRail extends Block
{
    //TODO move to stand-alone mod later so this can be used in place of MC's rail system
    public BlockRail()
    {
        super(Material.iron);
    }

    //TODO implement connections
    //      only same rails connect visually
    //TODO implement collision boxes
    //TODO implement renderer

    @Override
    public boolean isNormalCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_)
    {
        //TODO shape collision boxes based on meta
        AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_);

        if (axisalignedbb1 != null && p_149743_5_.intersectsWith(axisalignedbb1))
        {
            p_149743_6_.add(axisalignedbb1);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return Blocks.anvil.getIcon(0, 0);
    }

    @Override
    public int getMobilityFlag()
    {
        //Should be pushed by pistons, in theory?
        return 0;
    }

    /** Used for metadata look up to save metal sanity during coding. */
    public enum RailDirections
    {
        /** Strait rail Z axis, Also is SouthNorth direction */
        NorthSouth(ForgeDirection.UP, ForgeDirection.NORTH),
        /** Strait rail X axis, Also is WestEast direction */
        EastWest(ForgeDirection.UP, ForgeDirection.EAST),
        /** Strait rail Z axis, Also is SouthNorth direction */
        NorthSouthCeiling(ForgeDirection.DOWN, ForgeDirection.NORTH),
        /** Strait rail X axis, Also is WestEast direction */
        EastWestCeiling(ForgeDirection.DOWN, ForgeDirection.WEST),

        /** UpDown Rail */
        NorthFace(ForgeDirection.NORTH, ForgeDirection.UP),
        /** UpDown Rail */
        SouthFace(ForgeDirection.SOUTH, ForgeDirection.UP),
        /** UpDown Rail */
        EastFace(ForgeDirection.EAST, ForgeDirection.UP),
        /** UpDown Rail */
        WestFace(ForgeDirection.WEST, ForgeDirection.UP),

        /** UpDown Rail */
        NorthFaceSideWays(ForgeDirection.NORTH, ForgeDirection.EAST),
        /** UpDown Rail */
        SouthFaceSideWays(ForgeDirection.SOUTH, ForgeDirection.WEST),
        /** UpDown Rail */
        EastFaceSideWays(ForgeDirection.EAST, ForgeDirection.NORTH),
        /** UpDown Rail */
        WestFaceSideWaysm(ForgeDirection.WEST, ForgeDirection.SOUTH),

        /** Diagonal, Also is SouthWest */
        NorthEast(ForgeDirection.UP, ForgeDirection.UNKNOWN),
        /** Diagonal, Also is SouthEast */
        NorthWest(ForgeDirection.UP, ForgeDirection.UNKNOWN),
        /** Diagonal, Also is SouthWest */
        NorthEastCeiling(ForgeDirection.DOWN, ForgeDirection.UNKNOWN),
        /** Diagonal, Also is SouthEast */
        NorthWestCeiling(ForgeDirection.DOWN, ForgeDirection.UNKNOWN);

        public final ForgeDirection side;
        public final ForgeDirection facing;

        RailDirections(ForgeDirection side, ForgeDirection facing)
        {
            this.side = side;
            this.facing = facing;
        }

        public static RailDirections get(int meta)
        {
            if (meta >= meta && meta < values().length)
            {
                return values()[meta];
            }
            return NorthSouth;
        }
    }
}
