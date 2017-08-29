package com.builtbroken.icbm.content.blast.util;

import com.builtbroken.mc.api.edit.IWorldChangeLayeredAction;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.content.resources.gems.BlockGemOre;
import com.builtbroken.mc.core.content.resources.ore.BlockOre;
import com.builtbroken.mc.framework.explosive.blast.Blast;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.lib.world.edit.BlockEditMove;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Pull all the ore near the impact point to another point. By default this will cause all ore blocks to teleport to the surface of the world.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/28/2016.
 */
public class BlastOrePuller extends Blast<BlastOrePuller> implements IWorldChangeLayeredAction
{
    public static final List<Block> whiteList = new ArrayList();
    public static final List<Block> blackList = new ArrayList();

    private int layers;
    private int sideLength;
    private int sizeInt;

    public BlastOrePuller(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlastOrePuller setYield(double size)
    {
        double prev = this.size;
        super.setYield(Math.floor(size));
        if (prev != size)
        {
            calculateLayers();
        }
        return this;
    }

    public void calculateLayers()
    {
        sizeInt = (int) Math.floor(this.size);
        sideLength = sizeInt * 2 + 1;
        layers = sideLength * sideLength;
    }


    @Override
    public void getEffectedBlocks(List<IWorldEdit> list, int layer)
    {
        int gridX = layer % sideLength;
        int gridZ = layer / sideLength;

        int x = gridX + xi() - sizeInt;
        int z = gridZ + zi() - sizeInt;

        for (int y = 0; y < yi(); y++)
        {
            BlockPos pos = new BlockPos(x, y, z);
            IWorldEdit edit = changeBlock(pos);
            if (edit != null)
            {
                list.add(edit);
            }
        }

        createMovePositions(list);
    }

    protected void createMovePositions(List<IWorldEdit> list)
    {
        //Sort threw list and find them new homes
        Iterator<IWorldEdit> it = list.iterator();
        List<IWorldEdit> newList = new ArrayList();
        List<BlockPos> usedSurfacePos = new ArrayList();

        //Loop over all edits we have already mapped
        while (it.hasNext())
        {
            IWorldEdit edit = it.next();
            if (edit == null)
            {
                it.remove();
                continue;
            }
            int y = edit.yi(); //Start at edit location to prevent pulling ore down

            //Loop up to find a valid location to place the blocks
            while (y++ < 255)
            {
                BlockPos pos = new BlockPos(edit.xi(), y, edit.zi());

                //Ensure the slot is not already used
                if (!usedSurfacePos.contains(pos))
                {
                    //Ensure the slot can be replaced safely
                    if (pos.isReplaceable(oldWorld))
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
            //Add placement call
            newList.add(new BlockEditMove(edit, new Location(edit.oldWorld(), edit.x(), y, edit.z())).setAir());
            it.remove();
        }
        list.addAll(newList);
    }

    protected IWorldEdit changeBlock(BlockPos pos)
    {
        Block block = pos.getBlock(oldWorld);
        if (!pos.canSeeSky(oldWorld))
        {
            if (blackList.contains(block))
            {
                return null;
            }
            else if (block instanceof BlockOre || block instanceof net.minecraft.block.BlockOre || block instanceof BlockGemOre || whiteList.contains(block))
            {
                return new BlockEdit(oldWorld, pos).setAir();
            }
        }
        return null;
    }

    @Override
    public int getLayers()
    {
        return layers;
    }

    @Override
    public boolean shouldContinueAction(int layer)
    {
        return !killExplosion;
    }
}
