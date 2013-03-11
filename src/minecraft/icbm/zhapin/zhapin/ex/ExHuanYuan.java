package icbm.zhapin.zhapin.ex;

import icbm.api.ICBM;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ExHuanYuan extends ZhaPin
{
	public ExHuanYuan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
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

					ChunkProviderGenerate chunkProviderGenerate = ((ChunkProviderGenerate) ObfuscationReflectionHelper.getPrivateValue(ChunkProviderServer.class, chunkProviderServer, "currentChunkProvider", "d"));

					Chunk newChunk = chunkProviderGenerate.provideChunk(oldChunk.xPosition, oldChunk.zPosition);

					for (int x = 0; x < 16; x++)
					{
						for (int z = 0; z < 16; z++)
						{
							for (int y = 0; y < worldObj.getHeight(); y++)
							{
								int blockID = newChunk.getBlockID(x, y, z);
								int metadata = newChunk.getBlockMetadata(x, y, z);

								worldServer.setBlockAndMetadataWithNotify(x + oldChunk.xPosition * 16, y, z + oldChunk.zPosition * 16, blockID, metadata, 2);

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

		return false;
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "ICI", "CDC", "ICI", 'D', Block.blockDiamond, 'C', Item.pocketSundial, 'I', Block.blockSteel }), this.getUnlocalizedName(), ICBM.CONFIGURATION, true);
	}

	@Override
	public float getRadius()
	{
		return 16;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}
}
