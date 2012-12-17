package atomicscience.api;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockRadioactive extends Block
{
	public BlockRadioactive(int id, int texture, String textureFile)
	{
		super(id, texture, Material.ground);
		this.setTickRandomly(true);
		this.setHardness(0.2F);
		this.setLightValue(0.1F);
		this.setBlockName("radioactive");
		this.setTextureFile(textureFile);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		par1World.scheduleBlockUpdate(x, y, z, 20, 20);
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	public int getBlockTextureFromSideAndMetadata(int side, int par2)
	{
		return side == 1 ? this.blockIndexInTexture : (side == 0 ? this.blockIndexInTexture + 2 : this.blockIndexInTexture + 1);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	public void updateTick(World par1World, int x, int y, int z, Random par5Random)
	{
		if (!par1World.isRemote)
		{
			for (int var6 = 0; var6 < 4; ++var6)
			{
				int var7 = x + par5Random.nextInt(3) - 1;
				int var8 = y + par5Random.nextInt(5) - 3;
				int var9 = z + par5Random.nextInt(3) - 1;
				int var10 = par1World.getBlockId(var7, var8 + 1, var9);

				if (par5Random.nextFloat() > 0.8 && (par1World.getBlockId(var7, var8, var9) == Block.tilledField.blockID || par1World.getBlockId(var7, var8, var9) == Block.grass.blockID))
				{
					par1World.setBlockWithNotify(var7, var8, var9, this.blockID);
				}
			}

			int radius = 5;
			AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
			List<EntityLiving> entitiesNearby = par1World.getEntitiesWithinAABB(EntityLiving.class, bounds);

			for (EntityLiving entity : entitiesNearby)
			{
				PoisonRadiation.INSTANCE.poisonEntity((EntityLiving) entity);
			}

			if (par5Random.nextFloat() > 0.95)
			{
				par1World.setBlockWithNotify(x, y, z, Block.mycelium.blockID);
			}
		}
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		if (par5Entity instanceof EntityLiving)
		{
			PoisonRadiation.INSTANCE.poisonEntity((EntityLiving) par5Entity);
		}
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return Block.dirt.idDropped(0, par2Random, par3);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return null;
	}
}
