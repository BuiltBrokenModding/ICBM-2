package icbm.zhapin.zhapin.ex;

import icbm.api.explosion.ExplosionEvent;
import icbm.api.explosion.IExplosiveIgnore;
import icbm.core.ZhuYao;
import icbm.zhapin.EFeiBlock;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.EZhaDan;
import icbm.zhapin.zhapin.EZhaPin;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.List;

import mffs.api.IForceFieldBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExHongSu extends ZhaPin
{
	private static final int MAX_TAKE_BLOCKS = 5;

	public ExHongSu(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.isMobile = true;
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		if (!worldObj.isRemote)
		{
			worldObj.createExplosion(explosionSource, position.x, position.y, position.z, 5.0F, true);
		}
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		// Try to find and grab some blocks to orbit
		if (!worldObj.isRemote)
		{
			Vector3 currentPos;
			int blockID;
			int metadata;
			double dist;
			int takenBlocks = 0;

			loop:
			for (int r = 1; r < this.getRadius(); r++)
			{
				for (int x = -r; x < r; x++)
				{
					for (int y = -r; y < r; y++)
					{
						for (int z = -r; z < r; z++)
						{
							dist = MathHelper.sqrt_double((x * x + y * y + z * z));

							if (dist > r || dist < r - 2)
								continue;

							currentPos = new Vector3(position.x + x, position.y + y, position.z + z);
							blockID = worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());
							Block block = Block.blocksList[blockID];

							if (block != null)
							{
								if (block instanceof IForceFieldBlock)
								{
									((IForceFieldBlock) block).weakenForceField(worldObj, currentPos.intX(), currentPos.intY(), currentPos.intZ(), 50);
									continue;
								}

								if (block.getBlockHardness(worldObj, currentPos.intX(), currentPos.intY(), currentPos.intZ()) <= -1)
									continue;

								metadata = worldObj.getBlockMetadata(currentPos.intX(), currentPos.intY(), currentPos.intZ());

								int notify = 2;

								if (block instanceof BlockFluid)
								{
									notify = 0;
								}

								worldObj.setBlock(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0, 0, notify);

								if (block instanceof BlockFluid)
									continue;

								currentPos.add(0.5D);

								if (worldObj.rand.nextFloat() > 0.8)
								{
									EFeiBlock entity = new EFeiBlock(worldObj, currentPos, blockID, metadata);
									worldObj.spawnEntityInWorld(entity);
									entity.yawChange = 50 * worldObj.rand.nextFloat();
									entity.pitchChange = 50 * worldObj.rand.nextFloat();
								}

								takenBlocks++;
								if (takenBlocks > MAX_TAKE_BLOCKS)
									break loop;
							}
						}
					}
				}
			}
		}

		// Make the blocks controlled by this red matter orbit around it
		float radius = this.getRadius() + this.getRadius() / 2;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
		List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
		boolean explosionCreated = false;

		for (Entity entity : allEntities)
		{
			if (entity == explosionSource)
				continue;

			if (entity instanceof IExplosiveIgnore)
			{
				if (((IExplosiveIgnore) entity).canIgnore(new ExplosionEvent(worldObj, position.x, position.y, position.z, this)))
					continue;
			}

			if (entity instanceof EntityPlayer)
			{
				if (((EntityPlayer) entity).capabilities.isCreativeMode)
					continue;
			}

			double xDifference = entity.posX - position.x;
			double yDifference = entity.posY - position.y;
			double zDifference = entity.posZ - position.z;

			float r = radius;

			if (xDifference < 0)
				r = (int) -radius;

			entity.motionX -= (r - xDifference) * 0.002;

			r = radius;

			if (yDifference < 0)
				r = (int) -radius;

			entity.motionY -= (r - yDifference) * 0.005;

			r = radius;

			if (zDifference < 0)
				r = -radius;

			entity.motionZ -= (r - zDifference) * 0.002;

			if (entity instanceof EFeiBlock)
			{
				if (worldObj.isRemote)
				{
					if (ZhuYaoZhaPin.proxy.getParticleSetting() == 0)
					{
						if (worldObj.rand.nextInt(5) == 0)
						{
							ZhuYaoZhaPin.proxy.spawnParticle("digging", worldObj, new Vector3(entity), -xDifference, -yDifference + 10, -zDifference, ((EFeiBlock) entity).blockID, 0, ((EFeiBlock) entity).metadata, 2, 1);
						}
					}
				}
			}

			if (Vector3.distance(new Vector3(entity.posX, entity.posY, entity.posZ), position) < 4)
			{
				if (!explosionCreated && callCount % 5 == 0)
				{
					worldObj.createExplosion(explosionSource, entity.posX, entity.posY, entity.posZ, 3.0F, true);
					explosionCreated = true;
				}

				if (entity instanceof EntityLiving)
				{
					entity.fallDistance = 0;
				}
				else
				{
					if (entity instanceof EZhaPin)
					{
						if (((EZhaPin) entity).haoMa == ZhaPin.fanWuSu.getID())
						{
							worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

							if (worldObj.rand.nextFloat() > 0.85 && !worldObj.isRemote)
							{
								entity.setDead();
								return false;
							}
						}

					}
					else if (entity instanceof EZhaDan)
					{
						((EZhaDan) entity).explode();
					}
					else
					{
						entity.setDead();
					}
				}
			}
		}

		if (worldObj.rand.nextInt(10) == 0)
			worldObj.playSoundEffect(position.x + (Math.random() - 0.5) * radius, position.y + (Math.random() - 0.5) * radius, position.z + (Math.random() - 0.5) * radius, "icbm.collapse", 6.0F - worldObj.rand.nextFloat(), 1.0F - worldObj.rand.nextFloat() * 0.4F);

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.redmatter", 3.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);

		return true;
	}

	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * 
	 * @return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval()
	{
		return 1;
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', fanWuSu.getItemStack(), 'A', "strangeMatter" }), this.getUnlocalizedName(), ZhuYao.CONFIGURATION, true);
	}

	@Override
	public float getRadius()
	{
		return 35;
	}

	@Override
	public double getEnergy()
	{
		return 4000;
	}
}
