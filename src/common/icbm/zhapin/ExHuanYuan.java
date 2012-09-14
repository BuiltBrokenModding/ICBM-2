package icbm.zhapin;

import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.ChunkProviderServer;
import net.minecraft.src.Entity;
import net.minecraft.src.IChunkLoader;
import net.minecraft.src.Item;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import universalelectricity.Vector3;
import universalelectricity.basiccomponents.BasicComponents;
import universalelectricity.recipe.RecipeManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ExHuanYuan extends ZhaPin
{
	public ExHuanYuan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}
	
	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		if(!worldObj.isRemote)
		{
			try
			{
				Chunk oldChunk = worldObj.getChunkFromBlockCoords(position.intX(), position.intZ());
	
				WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().theWorldServer[worldObj.getWorldInfo().getDimension()];
				ChunkProviderServer chunkProviderServer = worldServer.theChunkProviderServer;
				
				ChunkProviderGenerate chunkProviderGenerate = ((ChunkProviderGenerate)ObfuscationReflectionHelper.getPrivateValue(ChunkProviderServer.class, chunkProviderServer, "currentChunkProvider"));
				IChunkLoader chunkLoader = ((IChunkLoader)ObfuscationReflectionHelper.getPrivateValue(ChunkProviderServer.class, chunkProviderServer, "currentChunkLoader"));
				               
				Chunk newChunk = chunkProviderGenerate.provideChunk(oldChunk.xPosition, oldChunk.zPosition);
				
				for(int x = 0; x < 16; x++)
				{
			        for(int z = 0; z < 16; z++)
			        {
			        	for(int y = 0; y < worldObj.getHeight(); y++)
		                {
			        		int blockID = newChunk.getBlockID(x, y, z);
			                int metadata = newChunk.getBlockMetadata(x, y, z);
		
			        		worldServer.setBlockAndMetadata(x+oldChunk.xPosition*16, y, z+oldChunk.zPosition*16, blockID, metadata); 
		                
							TileEntity tileEntity = newChunk.getChunkBlockTileEntity(x, y, z);
							
							if(tileEntity !=null)
							{
								worldServer.setBlockTileEntity(x+oldChunk.xPosition*16, y, z+oldChunk.zPosition*16, tileEntity);
							}
		                }
			        }
				}
				
				oldChunk.isTerrainPopulated = false;
				chunkProviderGenerate.populate(chunkProviderGenerate, oldChunk.xPosition, oldChunk.zPosition);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	@Override
	public void addCraftingRecipe()
	{
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"ICI", "CDC", "ICI", 'D', Block.blockDiamond, 'C', Item.pocketSundial, 'I', Block.blockSteel});
	}
}
