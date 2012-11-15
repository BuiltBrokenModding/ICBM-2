package icbm.zhapin.ex;

import icbm.ZhuYao;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.ChunkProviderServer;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.common.FMLCommonHandler;
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
			Chunk oldChunk = worldObj.getChunkFromBlockCoords(position.intX(), position.intZ());

			WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[worldObj.provider.dimensionId];
			ChunkProviderServer chunkProviderServer = worldServer.theChunkProviderServer;

			try
			{
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

							worldServer.setBlockAndMetadata(x + oldChunk.xPosition * 16, y, z + oldChunk.zPosition * 16, blockID, metadata);

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
			catch (Exception e)
			{
				System.out.println("ICBM Rejuvenation Failed! Contact developers!");
			}
		}

		return false;
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[]
		{ "ICI", "CDC", "ICI", 'D', Block.blockDiamond, 'C', Item.pocketSundial, 'I', Block.blockSteel }), this.getMing(), ZhuYao.CONFIGURATION, true);
	}
}
