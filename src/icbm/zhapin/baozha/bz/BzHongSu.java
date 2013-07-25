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
								if (takenBlocks > this.maxTakeBlocks)
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
		boolean doExplosion = true;

		for (Entity entity : allEntities)
		{
			doExplosion = !this.affectEntity(radius, entity, doExplosion);
		}
		/*
		 * if (this.worldObj.isRemote) { for (int i = 0; i < 10 * (2 -
		 * ZhuYaoZhaPin.proxy.getParticleSetting()); i++) { Vector3 randomVector = new
		 * Vector3(this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius(),
		 * this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius(),
		 * this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius());
		 * ZhuYaoZhaPin.proxy.spawnParticle("smoke", this.worldObj, Vector3.add(this.position,
		 * randomVector), 0, 0, 0, 1, 1, 1, 7.0F, 8); }
		 * 
		 * List<Entity> list = ZhuYaoZhaPin.proxy.getEntityFXs();
		 * 
		 * if (list != null) { for (Entity entity : list) { if (this.position.distanceTo(new
		 * Vector3(entity)) <= radius) { this.affectEntity(radius, entity, false); } } } }
		 */

		if (this.worldObj.rand.nextInt(8) == 0)
		{
			this.worldObj.playSoundEffect(position.x + (Math.random() - 0.5) * radius, position.y + (Math.random() - 0.5) * radius, position.z + (Math.random() - 0.5) * radius, ZhuYaoICBM.PREFIX + "collapse", 6.0F - this.worldObj.rand.nextFloat(), 1.0F - this.worldObj.rand.nextFloat() * 0.4F);
		}

		this.worldObj.playSoundEffect(position.x, position.y, position.z, ZhuYaoICBM.PREFIX + "redmatter", 3.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 1F);
	}

	/**
	 * Makes an entity get affected by Red Matter.
	 * 
	 * @Return True if explosion happened
	 */
	public boolean affectEntity(float radius, Entity entity, boolean doExplosion)
	{
		boolean explosionCreated = false;

		if (entity == this.controller)
		{
			return false;
		}

		if (entity instanceof IExplosiveIgnore)
		{
			if (((IExplosiveIgnore) entity).canIgnore(this))
			{
				return false;
			}
		}

		if (entity instanceof EntityPlayer)
		{
			if (((EntityPlayer) entity).capabilities.isCreativeMode)
			{
				return false;
			}
		}

		double xDifference = entity.posX - position.x;
		double yDifference = entity.posY - position.y;
		double zDifference = entity.posZ - position.z;

		/**
		 * The percentage of the closeness of the entity.
		 */
		double xPercentage = 1 - (xDifference / radius);
		double yPercentage = 1 - (yDifference / radius);
		double zPercentage = 1 - (zDifference / radius);
		double distancePercentage = (this.position.distanceTo(new Vector3(entity)) / radius);

		Vector3 entityPosition = new Vector3(entity);
		Vector3 centeredPosition = entityPosition.clone().subtract(this.position);
		centeredPosition.rotate(1.5 * distancePercentage * Math.random(), 1.5 * distancePercentage * Math.random(), 1.5 * distancePercentage * Math.random());
		Vector3 newPosition = Vector3.add(this.position, centeredPosition);
		// Orbit Velocity
		entity.addVelocity(newPosition.x - entityPosition.x, 0, newPosition.z - entityPosition.z);
		// Gravity Velocity
		entity.addVelocity(-xDifference * 0.015 * xPercentage, -yDifference * 0.015 * yPercentage, -zDifference * 0.015 * zPercentage);

		if (this.worldObj.isRemote)
		{
			if (entity instanceof EFeiBlock)
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
			if (doExplosion && !explosionCreated && callCount % 5 == 0)
			{
				/**
				 * Inject velocities to prevent this explosion to move RedMatter.
				 */
				Vector3 tempMotion = new Vector3(this.controller.motionX, this.controller.motionY, this.controller.motionZ);
				this.worldObj.createExplosion(this.exploder, entity.posX, entity.posY, entity.posZ, 3.0F, true);
				this.controller.motionX = tempMotion.x;
				this.controller.motionY = tempMotion.y;
				this.controller.motionZ = tempMotion.z;
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
							return explosionCreated;
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

		return explosionCreated;
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
		return this.callCount > 1;
	}
}
