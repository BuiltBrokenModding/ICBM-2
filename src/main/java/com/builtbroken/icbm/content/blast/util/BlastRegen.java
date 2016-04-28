package com.builtbroken.icbm.content.blast.util;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.transform.sorting.Vector3DistanceComparator;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import java.util.Collections;
import java.util.List;

/**
 * Regen missile that repopulates blocks in the target area only.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/11/2015.
 */
public class BlastRegen extends Blast<BlastRegen>
{
    @Override
    public void getEffectedBlocks(List<IWorldEdit> list)
    {
        int chunks = ((int) size / 16) + 1;
        int chunk_x = ((int) x >> 4);
        int chunk_z = ((int) z >> 4);

        IChunkProvider provider = world.getChunkProvider();

        if (provider instanceof ChunkProviderServer)
        {
            for (int cx = chunk_x - chunks; cx <= chunk_x + chunks; cx++)
            {
                for (int cz = chunk_z - chunks; cz <= chunk_z + chunks; cz++)
                {
                    Chunk newChunk = ((ChunkProviderServer) provider).currentChunkProvider.provideChunk(cx, cz);
                    //((ChunkProviderServer) provider).currentChunkProvider.populate(p_73153_1_, p_73153_2_, p_73153_3_);
                    //GameRegistry.generateWorld(p_73153_2_, p_73153_3_, worldObj, currentChunkProvider, p_73153_1_);
                    for (int i = 0; i < 16; i++)
                    {
                        for (int k = 0; k < 16; k++)
                        {
                            for (int j = 0; j < 255; j++)
                            {
                                int x = (cx << 4) + i;
                                int z = (cz << 4) + k;
                                if (shouldRegenLocation(x, j, z))
                                {
                                    Block block = newChunk.getBlock(i, j, k);
                                    int meta = newChunk.getBlockMetadata(i, j, k);
                                    BlockEdit edit = new BlockEdit(world, x, j, z).set(block, meta, false, true);
                                    if (edit.hasChanged())
                                    {
                                        list.add(edit);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(list, new Vector3DistanceComparator(new Pos(x, y, z)));
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!world.isRemote && !beforeBlocksPlaced)
        {
            IChunkProvider provider = world.getChunkProvider();

            if (provider instanceof ChunkProviderServer)
            {
                int chunks = ((int) size / 16) + 1;
                int chunk_x = ((int) x >> 4);
                int chunk_z = ((int) z >> 4);

                for (int cx = chunk_x - chunks; cx <= chunk_x + chunks; cx++)
                {
                    for (int cz = chunk_z - chunks; cz <= chunk_z + chunks; cz++)
                    {
                        Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
                        chunk.isTerrainPopulated = false;
                        chunk.isModified = true;
                        provider.populate(provider, cx, cz);
                    }
                }
            }
        }
    }

    /**
     * Checks if the location should be regenerated
     *
     * @return true if yes
     */
    protected boolean shouldRegenLocation(int x, int y, int z)
    {
        return y >= 0 && y <= 255;
    }


    @Override
    public int shouldThreadAction()
    {
        return 50;
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {

    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {

    }
}
