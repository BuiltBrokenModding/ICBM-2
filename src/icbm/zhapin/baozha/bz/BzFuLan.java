package icbm.zhapin.baozha.bz;

import icbm.core.ZhuYaoICBM;
import icbm.zhapin.baozha.BaoZha;
import icbm.zhapin.baozha.thr.ThrSheXian;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class BzFuLan extends BaoZha
{
	private ThrSheXian thread;
	private float nengLiang;

	public BzFuLan(World world, Entity entity, double x, double y, double z, float size)
	{
		super(world, entity, x, y, z, size);
	}

	public BzFuLan(World world, Entity entity, double x, double y, double z, float size, float nengLiang)
	{
		this(world, entity, x, y, z, size);
		this.nengLiang = nengLiang;
	}

	@Override
	public void doPreExplode()
	{
		if (!worldObj.isRemote)
		{
			this.thread = new ThrSheXian(worldObj, position, (int) this.getRadius(), this.nengLiang, this.exploder);
			this.thread.run();
		}
	}

	@Override
	public void doExplode()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.thread.isComplete)
			{
				for (Vector3 targetPosition : this.thread.results)
				{
					/**
					 * Decay the blocks.
					 */
					int blockID = targetPosition.getBlockID(worldObj);

					if (blockID > 0)
					{
						if (blockID == Block.grass.blockID || blockID == Block.sand.blockID)
						{
							if (worldObj.rand.nextFloat() > 0.96)
							{
								targetPosition.setBlock(worldObj, ZhuYaoICBM.bFuShe.blockID);
							}
						}

						if (blockID == Block.stone.blockID)
						{
							if (worldObj.rand.nextFloat() > 0.99)
							{
								targetPosition.setBlock(worldObj, ZhuYaoICBM.bFuShe.blockID);
							}
						}

						else if (blockID == Block.leaves.blockID)
						{
							targetPosition.setBlock(worldObj, 0);
						}
						else if (blockID == Block.tallGrass.blockID)
						{
							if (Math.random() * 100 > 50)
							{
								targetPosition.setBlock(worldObj, Block.cobblestone.blockID);
							}
							else
							{
								targetPosition.setBlock(worldObj, 0);
							}
						}
						else if (blockID == Block.tilledField.blockID)
						{
							targetPosition.setBlock(worldObj, ZhuYaoICBM.bFuShe.blockID);
						}
					}
				}
			}
		}
	}

	@Override
	public int proceduralInterval()
	{
		return 1;
	}

	@Override
	public float getEnergy()
	{
		return 100;
	}
}
