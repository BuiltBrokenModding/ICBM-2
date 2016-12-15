package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Blast that kills all plant life
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/2015.
 */
public class BlastPlantLife extends BlastSimplePath<BlastPlantLife>
{
    public BlastPlantLife(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlockEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        if (!location.isAirBlock() && location.getHardness() >= 0)
        {
            if (block == Blocks.dirt)
            {
                if(location.add(0, 1, 0).isAirBlock())
                {
                    return new BlockEdit(location).set(Blocks.grass, 0, false, true);
                }
                return new BlockEdit(location).set(Blocks.dirt, 0, false, true);
            }
            else if (block == Blocks.cobblestone && location.world.rand.nextBoolean())
            {
                return new BlockEdit(location).set(Blocks.mossy_cobblestone, 0, false, true);
            }
        }
        return null;
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {
        if (!world.isRemote)
        {
            //Generate random position near block
            double posX = (double) ((float) blocks.x() + world.rand.nextFloat());
            double posY = (double) ((float) blocks.y() + world.rand.nextFloat());
            double posZ = (double) ((float) blocks.z() + world.rand.nextFloat());

            Pos pos = randomMotion(posX, posY, posZ);
            //Spawn particles
            Engine.proxy.spawnParticle("explode", world, (posX + x * 1.0D) / 2.0D, (posY + y * 1.0D) / 2.0D, (posZ + z * 1.0D) / 2.0D, pos.x(), pos.y(), pos.z());
            Engine.proxy.spawnParticle("smoke", world, posX, posY, posZ, pos.x(), pos.y(), pos.z());
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {

    }
}
