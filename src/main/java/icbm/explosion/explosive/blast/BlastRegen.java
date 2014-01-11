package icbm.explosion.explosive.blast;

import icbm.explosion.explosive.ExplosionBase;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class BlastRegen extends ExplosionBase
{
	public BlastRegen(World world, Entity entity, double x, double y, double z, float size)
	{
		super(world, entity, x, y, z, size);
	}

	@Override
	public void doExplode()
	{
		if (!worldObj.isRemote)
		{
			try
			{
				Chunk oldChunk = worldObj.getChunkFromBlockCoords(position.intX(), position.intZ());

				if (worldObj instanceof WorldServer)
				{
					WorldServer worldServer = (WorldServer) worldObj;
					ChunkProviderServer chunkProviderServer = worldServer.theChunkProviderServer;
					IChunkProvider chunkProviderGenerate = ((IChunkProvider) ObfuscationReflectionHelper.getPrivateValue(ChunkProviderServer.class, chunkProviderServer, "currentChunkProvider", "d", "field_73246_d"));

					Chunk newChunk = chunkProviderGenerate.provideChunk(oldChunk.xPosition, oldChunk.zPosition);

					for (int x = 0; x < 16; x++)
					{
						for (int z = 0; z < 16; z++)
						{
							for (int y = 0; y < worldObj.getHeight(); y++)
							{
								int blockID = newChunk.getBlockID(x, y, z);
								int metadata = newChunk.getBlockMetadata(x, y, z);

								worldServer.setBlock(x + oldChunk.xPosition * 16, y, z + oldChunk.zPosition * 16, blockID, metadata, 2);

								TileEntity tileEntity = newChunk.getChunkBlockTileEntity(x, y, z);

								if (tileEntity != null)
								{
									worldServer.setBlockTileEntity(x + oldChunk.xPosition * 16, y, z + oldChunk.zPosition * 16, tileEntity);
								}
							}
						}
					}

					oldChunk.isTerrainPopulated = false;
					chunkProviderGenerate.populate(chunkProviderGenerate, oldChunk.xPosition, oldChunk.zPosition);
				}
			}
			catch (Exception e)
			{
				System.out.println("ICBM Rejuvenation Failed!");
				e.printStackTrace();
			}
		}
	}

	@Override
	public float getEnergy()
	{
		return 0;
	}
}
