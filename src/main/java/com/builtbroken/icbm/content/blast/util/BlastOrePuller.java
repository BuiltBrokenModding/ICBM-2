package com.builtbroken.icbm.content.blast.util;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.content.resources.BlockOre;
import com.builtbroken.mc.core.content.resources.gems.BlockGemOre;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.lib.world.edit.BlockEditMove;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Pull all the ore near the impact point to another point. By default this will cause all ore blocks to teleport to the surface of the world.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/28/2016.
 */
public class BlastOrePuller extends BlastSimplePath<BlastOrePuller>
{
    public static final List<Block> whiteList = new ArrayList();
    public static final List<Block> blackList = new ArrayList();

    public BlastOrePuller(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public void getEffectedBlocks(List<IWorldEdit> list)
    {
        super.getEffectedBlocks(list);

        //Sort threw list and find them new homes
        Iterator<IWorldEdit> it = list.iterator();
        List<IWorldEdit> newList = new ArrayList();
        List<Pos> usedSurfacePos = new ArrayList();

        //Loop over all edits we have already mapped
        while (it.hasNext())
        {
            IWorldEdit edit = it.next();
            if (edit == null)
            {
                it.remove();
                continue;
            }
            int y = 0;

            //Loop up to find a valid location to place the blocks
            while (y++ < 255)
            {
                Pos pos = new Pos((int) edit.x(), y, (int) edit.z());

                //Ensure the slot is not already used
                if (!usedSurfacePos.contains(pos))
                {
                    //Ensure the slot can be replaced safely
                    if (pos.isReplaceable(world))
                    {
                        usedSurfacePos.add(pos);
                        break;
                    }
                }
            }
            //Remove edit if that stack is too high
            //TODO have stack shift sideways
            if (y >= 255)
            {
                it.remove();
                continue;
            }
            //Add placement call TODO make new block edit class called BlockMove to ensure the original location is also still valid and to reverse if the final location is not valid
            newList.add(new BlockEditMove(edit, new Location(edit.world(), edit.x(), y, edit.z())).setAir());
            it.remove(); //Always remove as we now uses the move edit
        }
        list.addAll(newList);
    }

    @Override
    public IWorldEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        if (!location.canSeeSky())
        {
            if (blackList.contains(block))
            {
                return null;
            }
            else if (block instanceof BlockOre || block instanceof net.minecraft.block.BlockOre || block instanceof BlockGemOre || whiteList.contains(block))
            {
                return new BlockEdit(location).setAir();
            }
            else if (block.getUnlocalizedName().contains("ore"))
            {
                List<ItemStack> stacks = block.getDrops(location.world, location.xi(), location.yi(), location.zi(), 0, 0);
                if (stacks != null)
                {
                    for (ItemStack stack : stacks)
                    {
                        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(stack);
                        if (result != null && result.getUnlocalizedName().contains("ingot")) // If it can smelt it is ore
                        {
                            //Add to white list to increase speed
                            whiteList.add(block);
                            return new BlockEdit(location).setAir();
                        }
                    }
                }
                //Auto add to black list to increase speed
                blackList.add(block);
            }
        }
        return null;
    }
}
