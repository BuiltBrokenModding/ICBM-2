package icbm.zhapin.baozha.bz;

import icbm.api.explosion.IExplosiveIgnore;
import icbm.core.ZhuYaoICBM;
import icbm.zhapin.EFeiBlock;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.baozha.BaoZha;
import icbm.zhapin.baozha.EBaoZha;
import icbm.zhapin.zhapin.EZhaDan;

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
import net.minecraftforge.fluids.IFluidBlock;
import universalelectricity.core.vector.Vector3;

public class BzHongSu extends BaoZha
{
	private int maxTakeBlocks = 5;

	public BzHongSu(World world, Entity entity, double x, double y, double z, float size)
	{
		super(world, entity, x, y, z, size);
	}

	@Override
	public void doPreExplode()
	{
		if (!this.worldObj.isRemote)
		{
			this.worldObj.createExplosion(this.exploder, position.x, position.y, position.z, 5.0F, true);
		}
	}

	@Override
	public void doExplode()
	{
		// Try to find and grab some blocks to orbit
		if (!this.worldObj.isRemote)
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
							blockID = this.worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());
							Block block = Block.blocksList[blockID];

							if (block != null)
							{
								if (block instanceof IForceFieldBlock)
								{
									((IForceFieldBlock) block).weakenForceField(this.worldObj, currentPos.intX(), currentPos.intY(), currentPos.intZ(), 50);
									continue;
								}

								if (block.getBlockHardness(this.worldObj, currentPos.intX(), currentPos.intY(), currentPos.intZ()) <= -1)
									continue;

								metadata = this.worldObj.getBlockMetadata(currentPos.intX(), currentPos.intY(), currentPos.intZ());

								int notify = 2;

								if (block instanceof BlockFluid)
								{
									notify = 0;
								}

								this.worldObj.setBlock(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0, 0, notify);

								if (block instanceof BlockFluid || block instanceof IFluidBlock)
									continue;

								currentPos.add(0.5D);

								if (this.worldObj.rand.nextFloat() > 0.8)
								{
									EFeiBlock entity = new EFeiBlock(this.worldObj, currentPos, blockID, metadata);
									this.worldObj.spawnEntityInWorld(entity);
									entity.yawChange = 50 * this.worldObj.rand.nextFloat();
									entity.pitchChange = 50 * this.worldObj.rand.nextFloat();
								}

								takenBlocks++;
								if (takenBlocks > maxTakeBlocks)
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
		List<Entity> allEntities = this.worldObj.getEntitiesWithinAABB(Entity.class, bounds);
		boolean explosionCreated = false;

		for (Entity entity : allEntities)
		{
			if (entity == this.controller)
				continue;

			if (entity instanceof IExplosiveIgnore)
			{
				if (((IExplosiveIgnore) entity).canIgnore(this))
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
				if (this.worldObj.isRemote)
				{
					if (ZhuYaoZhaPin.proxy.getParticleSetting() == 0)
					{
						if (this.worldObj.rand.nextInt(5) == 0)
						{
							ZhuYaoZhaPin.proxy.spawnParticle("digging", this.worldObj, new Vector3(entity), -xDifference, -yDifference + 10, -zDifference, ((EFeiBlock) entity).blockID, 0, ((EFeiBlock) entity).metadata, 2, 1);
						}
					}
				}
			}

			if (Vector3.distance(new Vector3(entity.posX, entity.posY, entity.posZ), position) < 4)
			{
				if (!explosionCreated && callCount % 5 == 0)
				{
					this.worldObj.createExplosion(this.exploder, entity.posX, entity.posY, entity.posZ, 3.0F, true);
					explosionCreated = true;
				}

				if (entity instanceof EntityLiving)
				{
					entity.fallDistance = 0;
				}
				else
				{
					if (entity instanceof EBaoZha)
					{
						if (((EBaoZha) entity).baoZha instanceof BzFanWuSu || ((EBaoZha) entity).baoZha instanceof BzHongSu)
						{
							this.worldObj.playSoundEffect(position.x, position.y, position.z, ZhuYaoICBM.PREFIX + "explosion", 7.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

							if (this.worldObj.rand.nextFloat() > 0.85 && !this.worldObj.isRemote)
							{
								entity.setDead();
								return;
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

		if (this.worldObj.rand.nextInt(10) == 0)
			this.worldObj.playSoundEffect(position.x + (Math.random() - 0.5) * radius, position.y + (Math.random() - 0.5) * radius, position.z + (Math.random() - 0.5) * radius, ZhuYaoICBM.PREFIX + "collapse", 6.0F - this.worldObj.rand.nextFloat(), 1.0F - this.worldObj.rand.nextFloat() * 0.4F);

		this.worldObj.playSoundEffect(position.x, position.y, position.z, ZhuYaoICBM.PREFIX + "redmatter", 3.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 1F);
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
	public float getEnergy()
	{
		return -3000;
	}

	@Override
	public boolean isMovable()
	{
		return true;
	}
}
