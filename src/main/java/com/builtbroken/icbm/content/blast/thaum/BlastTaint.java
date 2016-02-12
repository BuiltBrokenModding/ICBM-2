package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import thaumcraft.common.config.ConfigBlocks;

/**
 * Spawns taint in an area around the impact point.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/15/2015.
 */
public class BlastTaint extends BlastSimplePath<BlastTaint>
{
    @Override
    public BlockEdit changeBlock(Location location)
    {
        //TODO apply taint flux to entities
        //TODO corrupt blocks around it
        //TODO spawn taint creatures if size > 5
        //TODO randomize placement
        /** {@link thaumcraft.common.lib.events.EventHandlerEntity#livingTick(LivingDeathEvent)} } */
        Block block = location.getBlock();
        int meta = location.getBlockMetadata();
        if (block == ConfigBlocks.blockFluxGoo && meta < 7)
        {
            return new BlockEdit(location, ConfigBlocks.blockFluxGoo, meta + 1);
        }
        else if (block == ConfigBlocks.blockFluxGas && meta < 7)
        {
            return new BlockEdit(location, ConfigBlocks.blockFluxGas, meta + 1);
        }
        else if (location.isAirBlock()) //TODO check for replaceable
        {
            if (location.world.rand.nextBoolean())
            {
                return new BlockEdit(location, ConfigBlocks.blockFluxGoo, 1);
            }
            else
            {
                return new BlockEdit(location, ConfigBlocks.blockFluxGas, 1);
            }
        }
        return null;
    }
}
