package com.builtbroken.icbm.content.rail;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.Engine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
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
    public static final float RAIL_HEIGHT = 0.4f;

    //TODO move to stand-alone mod later so this can be used in place of MC's rail system
    public BlockRail()
    {
        super(Material.iron);
        this.setBlockName(ICBM.PREFIX + "icbmMissileRail");
        this.setHardness(5);
        this.setResistance(5);
    }

    //TODO implement connections
    //      only same rails connect visually
    //TODO implement collision boxes
    //TODO implement renderer

    @Override
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.3f, 0f, 0f, 0.7f, RAIL_HEIGHT, 1f);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        final int meta = world.getBlockMetadata(x, y, z);
        final RailDirections railType = RailDirections.get(meta);
        if (railType == RailDirections.EastWest)
        {
            this.setBlockBounds(0f, 0f, 0.3f, 1f, RAIL_HEIGHT, 0.7f);
        }
        else if (railType == RailDirections.NorthSouthCeiling)
        {

        }
        else if (railType == RailDirections.EastWestCeiling)
        {

        }
        //Default to north
        else
        {
            this.setBlockBounds(0.3f, 0f, 0f, 0.7f, RAIL_HEIGHT, 1f);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        this.setBlockBoundsBasedOnState(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
        return super.getCollisionBoundingBoxFromPool(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
    }

    @Override @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
    {
        this.setBlockBoundsBasedOnState(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
        return super.getSelectedBoundingBoxFromPool(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xf, float yf, float zf)
    {
        if (Engine.runningAsDev && !world.isRemote)
        {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.stick)
            {
                player.addChatComponentMessage(new ChatComponentText("Type: " + RailDirections.get(world.getBlockMetadata(x, y, z))));
                return true;
            }
        }
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
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.anvil.getIcon(0, 0);
    }

    @Override
    public int getMobilityFlag()
    {
        //Should be pushed by pistons, in theory?
        return 0;
    }

    @Override
    public boolean isNormalCube()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
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
