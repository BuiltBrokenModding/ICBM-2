package com.builtbroken.icbm.content.rail;

import com.builtbroken.mc.lib.helper.BlockUtility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/31/2016.
 */
public class ItemBlockRail extends ItemBlock
{
    public ItemBlockRail(Block block)
    {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        //TODO enforce rails of different types can not be placed next to each other
        //      This to correct a potential issue of ceiling and floor rails causing carts to switch places
        ForgeDirection facing = BlockUtility.determineForgeDirection(player);
        ForgeDirection attachedSide = ForgeDirection.getOrientation(side);
        if (attachedSide == ForgeDirection.UP)
        {
            if (facing == ForgeDirection.EAST || facing == ForgeDirection.WEST)
            {
                metadata = BlockRail.RailDirections.EastWest.ordinal();
            }
            else
            {
                metadata = BlockRail.RailDirections.NorthSouth.ordinal();
            }
        }
        else if (attachedSide == ForgeDirection.DOWN)
        {
            if (facing == ForgeDirection.EAST || facing == ForgeDirection.WEST)
            {
                metadata = BlockRail.RailDirections.EastWestCeiling.ordinal();
            }
            else
            {
                metadata = BlockRail.RailDirections.NorthSouthCeiling.ordinal();
            }
        }
        //North
        else if (side == 2)
        {
            //Click left or right side (Not middle)
            if (hitX <= 0.3 || hitX >= 0.7)
            {
                metadata = BlockRail.RailDirections.NorthFaceSideWays.ordinal();
            }
            else
            {
                metadata = BlockRail.RailDirections.NorthFace.ordinal();
            }
        }
        //South
        else if (side == 3)
        {
            //Click left or right side (Not middle)
            if (hitX <= 0.3 || hitX >= 0.7)
            {
                metadata = BlockRail.RailDirections.SouthFaceSideWays.ordinal();
            }
            else
            {
                metadata = BlockRail.RailDirections.SouthFace.ordinal();
            }
        }
        //West
        else if (side == 4)
        {
            //Click left or right side (Not middle)
            if (hitZ <= 0.3 || hitZ >= 0.7)
            {
                metadata = BlockRail.RailDirections.WestFaceSideWays.ordinal();
            }
            else
            {
                metadata = BlockRail.RailDirections.WestFace.ordinal();
            }
        }
        //East
        else if (side == 5)
        {
            //Click left or right side (Not middle)
            if (hitZ <= 0.3 || hitZ >= 0.7)
            {
                metadata = BlockRail.RailDirections.EastFaceSideWays.ordinal();
            }
            else
            {
                metadata = BlockRail.RailDirections.EastFace.ordinal();
            }
        }
        //TODO finish implementing sides
        return world.setBlock(x, y, z, field_150939_a, metadata, 3);
    }
}
