package icbm.zhapin.baozha.bz;

import icbm.core.ZhuYaoICBM;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.baozha.BaoZha;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class BzWan extends BaoZha
{
	public int duration = 20 * 8;
	private Vector3 teleportTarget;

	public BzWan(World world, Entity entity, double x, double y, double z, float size, Vector3 teleportTarget)
	{
		super(world, entity, x, y, z, size);
		this.teleportTarget = teleportTarget;
	}

	@Override
	public void doExplode()
	{
		if (this.worldObj.isRemote)
		{
			int r = (int) (this.getRadius() - ((double) this.callCount / (double) this.duration) * this.getRadius());

			for (int x = -r; x < r; x++)
			{
				for (int z = -r; z < r; z++)
				{
					for (int y = -r; y < r; y++)
					{
						Vector3 targetPosition = Vector3.add(position, new Vector3(x, y, z));

						double distance = targetPosition.distanceTo(position);

						if (distance < r && distance > r - 1)
						{
							if (targetPosition.getBlockID(worldObj) != 0)
								continue;

							if (worldObj.rand.nextFloat() < Math.max(0.001 * r, 0.01))
							{
								float velX = (float) ((targetPosition.x - position.x) * 0.5);
								float velY = (float) ((targetPosition.y - position.y) * 0.5);
								float velZ = (float) ((targetPosition.z - position.z) * 0.5);

								ZhuYaoZhaPin.proxy.spawnParticle("portal", worldObj, targetPosition, velX, velY, velZ, 5f, 1);
							}
						}
					}
				}
			}
		}

		int radius = (int) this.getRadius();
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
		List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
		boolean explosionCreated = false;

		for (Entity entity : allEntities)
		{
			if (entity != this.controller)
			{

				double xDifference = entity.posX - position.x;
				double yDifference = entity.posY - position.y;
				double zDifference = entity.posZ - position.z;

				int r = (int) this.getRadius();
				if (xDifference < 0)
					r = (int) -this.getRadius();

				entity.motionX -= (r - xDifference) * Math.abs(xDifference) * 0.0006;

				r = (int) this.getRadius();
				if (entity.posY > position.y)
					r = (int) -this.getRadius();
				entity.motionY += (r - yDifference) * Math.abs(yDifference) * 0.0011;

				r = (int) this.getRadius();
				if (zDifference < 0)
					r = (int) -this.getRadius();

				entity.motionZ -= (r - zDifference) * Math.abs(zDifference) * 0.0006;

				if (Vector3.distance(new Vector3(entity.posX, entity.posY, entity.posZ), position) < 4)
				{
					if (!explosionCreated && callCount % 5 == 0)
					{
						worldObj.spawnParticle("hugeexplosion", entity.posX, entity.posY, entity.posZ, 0.0D, 0.0D, 0.0D);
						explosionCreated = true;
					}

					try
					{
						if (this.teleportTarget != null)
						{
							entity.posX = this.teleportTarget.x;
							entity.posY = this.teleportTarget.y;
							entity.posZ = this.teleportTarget.z;
						}
						else
						{
							if (entity.worldObj.provider.dimensionId == 1)
							{
								entity.travelToDimension(0);
							}
							else
							{
								entity.travelToDimension(1);
							}
						}
					}
					catch (Exception e)
					{
						ZhuYaoICBM.LOGGER.severe("Failed to teleport entity to the End.");
					}
				}
			}
		}

		this.worldObj.playSound(this.position.x, this.position.y, this.position.z, "portal.portal", 2F, worldObj.rand.nextFloat() * 0.4F + 0.8F, false);

		if (this.callCount > this.duration)
		{
			this.controller.endExplosion();
		}
	}

	@Override
	public void postExplode()
	{
		super.postExplode();

		if (!this.worldObj.isRemote)
		{
			for (int i = 0; i < 20; i++)
			{
				EntityEnderman enderman = new EntityEnderman(worldObj);
				enderman.setPosition(this.position.x, this.position.y, this.position.z);
				this.worldObj.spawnEntityInWorld(enderman);
			}
		}
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
	public float getRadius()
	{
		return 20;
	}

	@Override
	public float getEnergy()
	{
		return 0;
	}

	@Override
	public boolean isMovable()
	{
		return true;
	}
}
