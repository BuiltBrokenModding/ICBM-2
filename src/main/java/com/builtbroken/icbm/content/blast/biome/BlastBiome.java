package com.builtbroken.icbm.content.blast.biome;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketBiomeData;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.world.chunk.Chunk;

/**
 * Re-maps biome ids in an area
 *
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
        if (!world.isRemote && !beforeBlocksPlaced)
        {
            int range = (int) size * 10;

            int startX = (int) x();
            int startZ = (int) z();
            for (int x = startX - range; x <= (startX + range); x++)
            {
                for (int z = startZ - range; z <= (startZ + range); z++)
                {
                    Chunk chunk = world.getChunkFromBlockCoords(x, z);
                    chunk.getBiomeArray()[(x & 15) * 16 + (z & 15)] = biomeID;
                    chunk.isModified = true;
                }
            }

            Cube cube = new Cube(startX - range, 0, startZ - range, startX + range, 255, startZ + range);
            for (Chunk chunk : cube.getChunks(world))
            {
                PacketBiomeData packet = new PacketBiomeData(chunk);
                Engine.instance.packetHandler.sendToAllAround(packet, new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, Math.max(range, 100)));
            }
        }
    }
}
