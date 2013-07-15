package icbm.zhapin.baozha.bz;

import icbm.core.ZhuYaoICBM;
import icbm.zhapin.baozha.BaoZha;
import icbm.zhapin.baozha.EBaoZha;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class BzFanWuSu extends BaoZha
{
	private boolean destroyBedrock;

	public BzFanWuSu(World world, Entity entity, double x, double y, double z, float size)
	{
		super(world, entity, x, y, z, size);
	}

	public BzFanWuSu(World world, Entity entity, double x, double y, double z, float size, boolean destroyBedrock)
	{
		this(world, entity, x, y, z, size);
		this.destroyBedrock = destroyBedrock;
	}

	/**
	 * Called before an explosion happens
	 */
	@Override
	public void doPreExplode()
	{
		super.doPreExplode();
		this.worldObj.playSoundEffect(this.position.x, this.position.y, this.position.z, ZhuYaoICBM.PREFIX + "antimatter", 7F, (float) (this.worldObj.rand.nextFloat() * 0.1 + 0.9F));
		this.doDamageEntities(this.getRadius() * 2, Integer.MAX_VALUE);
	}

	@Override
	public void doExplode()
	{
		if (!this.worldObj.isRemote)
		{
			for (int x = (int) -this.getRadius(); x < this.getRadius(); x++)
			{
				for (int y = (int) -this.getRadius(); y < this.getRadius(); y++)
				{
					for (int z = (int) -this.getRadius(); z < this.getRadius(); z++)
					{
						Vector3 targetPosition = Vector3.add(position, new Vector3(x, y, z));
						double dist = position.distanceTo(targetPosition);

						if (dist < this.getRadius())
						{
							int blockID = targetPosition.getBlockID(worldObj);

							if (blockID > 0)
							{
								if (blockID == Block.bedrock.blockID && !this.destroyBedrock)
									continue;

								if (dist < this.getRadius() - 1 || worldObj.rand.nextFloat() > 0.7)
								{
									targetPosition.setBlock(worldObj, 0);
								}
							}
						}
					}

				}
			}
		}

		// TODO: Render shockwave
		/*
		 * else if (ZhuYao.proxy.isGaoQing()) { for (int x = -this.getRadius(); x <
		 * this.getRadius(); x++) { for (int y = -this.getRadius(); y < this.getRadius(); y++) { for
		 * (int z = -this.getRadius(); z < this.getRadius(); z++) { Vector3 targetPosition =
		 * Vector3.add(position, new Vector3(x, y, z)); double distance =
		 * position.distanceTo(targetPosition);
		 * 
		 * if (targetPosition.getBlockID(worldObj) == 0) { if (distance < this.getRadius() &&
		 * distance > this.getRadius() - 1 && worldObj.rand.nextFloat() > 0.5) {
		 * ParticleSpawner.spawnParticle("antimatter", worldObj, targetPosition); } } } } } }
		 */
	}

	@Override
	public void doPostExplode()
	{
		this.doDamageEntities(this.getRadius() * 2, Integer.MAX_VALUE);
	}

	@Override
	protected boolean onDamageEntity(Entity entity)
	{
		if (entity instanceof EBaoZha)
		{
			if (((EBaoZha) entity).baoZha instanceof BzHongSu)
			{
				entity.setDead();
				return true;
			}
		}

		return false;
	}

	@Override
	public float getEnergy()
	{
		return 30000000;
	}
}
