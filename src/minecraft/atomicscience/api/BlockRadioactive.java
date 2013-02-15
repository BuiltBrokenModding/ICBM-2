package atomicscience.api;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class BlockRadioactive extends Block
{
	public static int RECOMMENDED_ID = 3768;
	public boolean canSpread = true;
	public float radius = 5;
	public int amplifier = 2;
	public boolean canWalkPoison = true;

	public BlockRadioactive(int id, int texture, Material material)
	{
		super(id, texture, material);
		this.setTickRandomly(true);
	}

	public BlockRadioactive(int id, int texture, String textureFile)
	{
		this(id, texture, Material.ground);
		this.setHardness(0.2F);
		this.setLightValue(0.1F);
		this.setBlockName("radioactive");
		this.setTextureFile(textureFile);
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public int getBlockTextureFromSide(int side)
	{
		return side == 1 ? this.blockIndexInTexture : (side == 0 ? this.blockIndexInTexture + 2 : this.blockIndexInTexture + 1);
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		if (world.rand.nextFloat() > 0.8)
		{
			this.updateTick(world, x, y, z, world.rand);
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		if (!world.isRemote)
		{
			AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - this.radius, y - this.radius, z - this.radius, x + this.radius, y + this.radius, z + this.radius);
			List<EntityLiving> entitiesNearby = world.getEntitiesWithinAABB(EntityLiving.class, bounds);

			for (EntityLiving entity : entitiesNearby)
			{
				PoisonRadiation.INSTANCE.poisonEntity(new Vector3(x, y, z), (EntityLiving) entity, amplifier);
			}

			if (this.canSpread)
			{
				for (int i = 0; i < 4; ++i)
				{
					int newX = x + rand.nextInt(3) - 1;
					int newY = y + rand.nextInt(5) - 3;
					int newZ = z + rand.nextInt(3) - 1;
					int var10 = world.getBlockId(newX, newY + 1, newZ);

					if (rand.nextFloat() > 0.5 && (world.getBlockId(newX, newY, newZ) == Block.tilledField.blockID || world.getBlockId(newX, newY, newZ) == Block.grass.blockID))
					{
						world.setBlockWithNotify(newX, newY, newZ, this.blockID);
					}
				}

				if (rand.nextFloat() > 0.99)
				{
					world.setBlockWithNotify(x, y, z, Block.mycelium.blockID);
				}
			}
		}
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World par1World, int x, int y, int z, Entity par5Entity)
	{
		if (par5Entity instanceof EntityLiving && this.canWalkPoison)
		{
			PoisonRadiation.INSTANCE.poisonEntity(new Vector3(x, y, z), (EntityLiving) par5Entity);
		}
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}
}
