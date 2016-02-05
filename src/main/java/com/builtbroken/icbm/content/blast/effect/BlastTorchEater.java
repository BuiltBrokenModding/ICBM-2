package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Blast that easts all torch
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/7/2015.
 */
public class BlastTorchEater extends BlastSimplePath
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        //TODO maybe make path only air blocks for balance
        Block block = location.getBlock();
        if (block == Blocks.torch)
        {
            return new BlockEdit(location).set(Blocks.air, 0, true, true);
        }
        return null;
    }
}
