package icbm.zhapin.baozha.ex;

import icbm.zhapin.EFeiBlock;
import icbm.zhapin.EGuang;
import icbm.zhapin.baozha.BaoZha;
import icbm.zhapin.baozha.thr.ThrSheXian;
import icbm.zhapin.baozha.thr.ThrXunZhao;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

/**
 * Used by Exothermic and Endothermic explosions.
 * 
 * @author Calclavia
 * 
 */
public abstract class BzGuang extends BaoZha
{
	protected ThrXunZhao thread;
	protected Set<EFeiBlock> feiBlocks = new HashSet<EFeiBlock>();
	protected EGuang lightBeam;
	protected float red, green, blue;
	/** Radius in which the uplighting of blocks takes place */
	protected int radius = 5;

	public BzGuang(World world, Entity entity, double x, double y, double z, float size)
	{
		super(world, entity, x, y, z, size);
	}

	/**
	 * Called before an explosion happens
	 */
	@Override
	public void doPreExplode()
	{
		if (!this.worldObj.isRemote)
		{
			this.thread = new ThrXunZhao(this.worldObj, this.position, (int) this.getRadius(), this.exploder);
			this.thread.run();

			this.lightBeam = new EGuang(this.worldObj, position, 20 * 20, this.red, this.green, this.blue);
			this.worldObj.spawnEntityInWorld(this.lightBeam);
			this.worldObj.createExplosion(null, position.x, position.y, position.z, 4F, true);

		}
	}

	@Override
	public void doExplode()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.callCount > 35 && this.thread.isComplete)
			{
				this.controller.endExplosion();
			}

			if (this.canFocusBeam(this.worldObj, position))
			{
				Vector3 currentPos;
				int blockID;
				int metadata;
				double dist;

				int r = radius;

				for (int x = -r; x < r; x++)
				{
					for (int y = -r; y < r; y++)
					{
						for (int z = -r; z < r; z++)
						{
							dist = MathHelper.sqrt_double((x * x + y * y + z * z));

							if (dist > r || dist < r - 3)
								continue;
							currentPos = new Vector3(position.x + x, position.y + y, position.z + z);
							blockID = this.worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());

							if (blockID == 0 || blockID == Block.bedrock.blockID || blockID == Block.obsidian.blockID)
								continue;

							metadata = this.worldObj.getBlockMetadata(currentPos.intX(), currentPos.intY(), currentPos.intZ());

							if (this.worldObj.rand.nextInt(3) > 0)
							{
								this.worldObj.setBlock(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0, 0, 2);

								currentPos.add(0.5D);
								EFeiBlock entity = new EFeiBlock(this.worldObj, currentPos, blockID, metadata);
								this.worldObj.spawnEntityInWorld(entity);
								this.feiBlocks.add(entity);
								entity.pitchChange = 50 * this.worldObj.rand.nextFloat();
							}
						}
					}
				}
			}
			else
			{
				this.controller.endExplosion();
			}

			for (EFeiBlock entity : this.feiBlocks)
			{
				double xDifference = entity.posX - position.x;
				double zDifference = entity.posZ - position.z;

				int r = radius;
				if (xDifference < 0)
					r = -radius;

				if (xDifference > 4)
				{
					entity.motionX += (r - xDifference) * -0.02 * this.worldObj.rand.nextFloat();
				}

				if (entity.posY < position.y + 15)
				{
					entity.motionY += 0.5 + 0.6 * this.worldObj.rand.nextFloat();

					if (entity.posY < position.y + 3)
					{
						entity.motionY += 1.5;
					}
				}

				r = radius;

				if (zDifference < 0)
				{
					r = -radius;
				}

				if (zDifference > 4)
				{
					entity.motionZ += (r - zDifference) * -0.02 * this.worldObj.rand.nextFloat();
				}

				entity.yawChange += 3 * this.worldObj.rand.nextFloat();
			}
		}
	}

	@Override
	public void doPostExplode()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.lightBeam != null)
			{
				this.lightBeam.setDead();
				this.lightBeam = null;
			}

			/**
			 * Scatter all flying blocks away.
			 */
			if (this.canFocusBeam(this.worldObj, position))
			{
				for (EFeiBlock entity : this.feiBlocks)
				{
					double xDifference = entity.posX - position.x;
					double zDifference = entity.posZ - position.z;

					int m = 1;

					if (xDifference < 0)
						m = -1;

					entity.motionX += m * 5 * this.worldObj.rand.nextFloat();

					m = 1;

					if (zDifference < 0)
						m = -1;

					entity.motionZ += m * 5 * this.worldObj.rand.nextFloat();
				}
			}
		}
	}

	public boolean canFocusBeam(World worldObj, Vector3 position)
	{
		return worldObj.canBlockSeeTheSky(position.intX(), position.intY() + 1, position.intZ());
	}

	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * 
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval()
	{
		return 4;
	}

	@Override
	public float getEnergy()
	{
		return 10000;
	}
}
