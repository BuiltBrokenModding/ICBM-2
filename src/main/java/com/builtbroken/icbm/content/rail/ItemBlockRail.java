package com.builtbroken.icbm.content.rail;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
        if (side == 0 || side == 1)
        {
            //Left & right are inverted for South
            boolean left = hitX <= 0.3;
            boolean right = hitX >= 0.7;

            if (!left && !right)
            {
                metadata = BlockRail.RailDirections.NorthSouth.ordinal();
            }
            else
            {
                metadata = BlockRail.RailDirections.EastWest.ordinal();
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
