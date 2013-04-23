package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYao;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.ex.ThrSheXian.IThreadCallBack;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ExFuLan extends ZhaPin implements IThreadCallBack
{
	public ExFuLan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		super.doBaoZha(worldObj, position, explosionSource);

		if (!worldObj.isRemote)
		{
			new ThrSheXian(worldObj, position, radius, 200, this, explosionSource).run();
		}

		/*
		 * if (!worldObj.isRemote) { for (int x = -radius; x < radius; x++) { for (int y = -radius;
		 * y < radius; y++) { for (int z = -radius; z < radius; z++) { double dist =
		 * MathHelper.sqrt_double((x * x + y * y + z * z));
		 * 
		 * if (dist > radius) continue;
		 * 
		 * } } } }
		 */

		return false;
	}

	@Override
	public void onThreadComplete(ThrSheXian thread)
	{
		World worldObj = thread.world;
		Vector3 position = thread.position;

		if (!worldObj.isRemote)
		{
			for (Vector3 targetPosition : thread.destroyed)
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
							targetPosition.setBlock(worldObj, ZhuYao.bFuShe.blockID);
						}
					}

					if (blockID == Block.stone.blockID)
					{
						if (worldObj.rand.nextFloat() > 0.99)
						{
							targetPosition.setBlock(worldObj, ZhuYao.bFuShe.blockID);
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
						targetPosition.setBlock(worldObj, ZhuYao.bFuShe.blockID);
					}
				}
			}
		}
	}

	@Override
	public float getRadius()
	{
		return 50;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}

}
