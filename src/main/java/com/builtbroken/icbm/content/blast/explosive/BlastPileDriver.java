package com.builtbroken.icbm.content.blast.explosive;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Collections;
import java.util.List;

/**
 * Works like Reika's pile driver to break up the ground. Covers cobble stone to gravel, stone to cobble, gravel to sand, and can cause gas to leak to the surface.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2015.
 */
public class BlastPileDriver extends BlastSimplePath
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        //TODO add gravel and sand version of ores
        //TODO add compatibility for other mods
        //TODO add registry for mods to add blocks to this explosive
        //TODO add stone pile for destroyed stone objects
        //      For example walls, furnaces, etc
        //TODO maybe add status effect ( keeps firing explosive for X ticks)
        //TODO add screen shaking near explosive
        //TODO throw entities around while effect happens
        //TODO make placement of blocks random
        //TODO increase delay of placement
        //TODO teleport around sand and gravel (in other words throw)

        if (location.world.rand.nextBoolean())
        {
            if (block == Blocks.stone)
            {
                return new BlockEdit(location, Blocks.cobblestone);
            }
            else if (block == Blocks.stonebrick)
            {
                //TODO set to cracked bricks
                return new BlockEdit(location, Blocks.cobblestone);
            }
            else if (block == Blocks.cobblestone)
            {
                return new BlockEdit(location, Blocks.gravel);
            }
            else if (block == Blocks.gravel)
            {
                return new BlockEdit(location, Blocks.sand);
            }
        }
        return null;
    }

    @Override
    public void getEffectedBlocks(List<IWorldEdit> list)
    {
        super.getEffectedBlocks(list);
        Collections.shuffle(list);
    }
}
