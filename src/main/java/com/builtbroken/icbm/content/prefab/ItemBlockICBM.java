package com.builtbroken.icbm.content.prefab;

import com.builtbroken.mc.lib.helper.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/19/2016.
 */
public class ItemBlockICBM extends ItemBlock
{
    protected int additionalHeight = 0;

    public ItemBlockICBM(Block block)
    {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        super.addInformation(stack, player, lines, b);
        String localization = LanguageUtility.getLocal(getUnlocalizedName() + ".info");
        if (localization != null && !localization.isEmpty())
        {
            String[] split = localization.split(",");
            for (String line : split)
            {
                lines.add(line.trim());
            }
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xHit, float yHit, float zHit)
    {
        if (stack.stackSize > 0)
        {
            Block block = world.getBlock(x, y, z);

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

            if (y < 255 - additionalHeight)
            {
                if (player.canPlayerEdit(x, y, z, side, stack))
                {
                    if (world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, player, stack))
                    {
                        if (additionalHeight > 0)
                        {
                            for (int i = 1; i <= additionalHeight; i++)
                            {
                                if (!player.canPlayerEdit(x, y, z, side, stack))
                                {
                                    return true;
                                }
                                block = world.getBlock(x, y + i, z);
                                if (!block.isAir(world, x, y + i, z) && !block.isReplaceable(world, x, y + i, z))
                                {
                                    return true;
                                }
                            }
                        }
                        int meta = this.field_150939_a.onBlockPlaced(world, x, y, z, side, xHit, yHit, zHit, this.getMetadata(stack.getItemDamage()));

                        if (placeBlockAt(stack, player, world, x, y, z, side, xHit, yHit, zHit, meta))
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
