package com.builtbroken.icbm.content.fof;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/12/2016.
 */
public class ItemBlockFoF extends ItemBlock
{
    public ItemBlockFoF(Block block)
    {
        super(block);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xx, float yy, float zz)
    {
        if (stack.stackSize > 0)
        {
            Block block = world.getBlock(x, y, z);

            //Get placement side
            if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
            {
                side = 1;
            }
            else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
            {
                if (side == 0)
                {
                    --y;
                }

                if (side == 1)
                {
                    ++y;
                }

                if (side == 2)
                {
                    --z;
                }

                if (side == 3)
                {
                    ++z;
                }

                if (side == 4)
                {
                    --x;
                }

                if (side == 5)
                {
                    ++x;
                }
            }
            //Do checks to ensure we can place the block
            //Block is 2m tall so will take up 254 and 255, world limit is 255 so min y is (limit - 1)
            if (y < 254 && player.canPlayerEdit(x, y, z, side, stack))
            {
                Block block2 = world.getBlock(x, y + 1, z);
                if ((block2.isAir(world, x, y + 1, z) || block2.isReplaceable(world, x, y + 1, z)) && player.canPlayerEdit(x, y + 1, z, side, stack))
                {
                    if (world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, player, stack))
                    {
                        int meta = this.field_150939_a.onBlockPlaced(world, x, y, z, side, xx, yy, zz, this.getMetadata(stack.getItemDamage()));

                        if (placeBlockAt(stack, player, world, x, y, z, side, xx, yy, zz, meta))
                        {
                            world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), this.field_150939_a.stepSound.func_150496_b(), (this.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, this.field_150939_a.stepSound.getPitch() * 0.8F);
                            --stack.stackSize;
                        }

                        return true;
                    }
                }
            }
        }
        return false;
    }
}
