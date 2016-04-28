package com.builtbroken.icbm.content.blast.biome;

import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.world.chunk.Chunk;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/28/2016.
 */
public class BlastBiome extends Blast<BlastBiome>
{
    public final byte biomeID;

    public BlastBiome(byte id)
    {
        this.biomeID = id;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if(!world.isRemote && !beforeBlocksPlaced)
        {
            int range = (int)size;

            for(int x = (int)x() - range; x < (x + range); x++)
            {
                for(int z = (int)z() - range; z < (z + range); z++)
                {
                    Chunk chunk = world.getChunkFromBlockCoords(x, z);
                    chunk.getBiomeArray()[(x % 16) * 16 + (z % 16)] = biomeID;
                }
            }
        }
    }
}
