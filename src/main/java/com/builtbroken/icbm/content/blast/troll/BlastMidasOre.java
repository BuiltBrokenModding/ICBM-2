package com.builtbroken.icbm.content.blast.troll;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.content.resources.gems.BlockGemOre;
import com.builtbroken.mc.core.content.resources.ore.BlockOre;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns all ore into gold ore - Suggestion by Graugger 5/23/2017 6pm Est
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/23/2017.
 */
public class BlastMidasOre extends BlastSimplePath<BlastMidasOre>
{
    public static final List<Block> whiteList = new ArrayList();
    public static final List<Block> blackList = new ArrayList();

    public BlastMidasOre(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public IWorldEdit changeBlock(BlockPos location)
    {
        Block block = location.getBlock(oldWorld);

        if (blackList.contains(block))
        {
            return null;
        }
        else if (block instanceof BlockOre || block instanceof net.minecraft.block.BlockOre || block instanceof BlockGemOre || whiteList.contains(block))
        {
            return new BlockEdit(oldWorld, location).set(Blocks.gold_ore);
        }
        else if (block.getUnlocalizedName().contains("ore"))
        {
            List<ItemStack> stacks = block.getDrops(oldWorld, location.xi(), location.yi(), location.zi(), 0, 0);
            if (stacks != null)
            {
                for (ItemStack stack : stacks)
                {
                    ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(stack);
                    if (result != null && result.getUnlocalizedName().contains("ingot")) // If it can smelt it is ore
                    {
                        //Add to white list to increase speed
                        whiteList.add(block);
                        return new BlockEdit(oldWorld, location).set(Blocks.gold_ore);
                    }
                }
            }
            //Auto add to black list to increase speed
            blackList.add(block);
        }
        return null;
    }
}
