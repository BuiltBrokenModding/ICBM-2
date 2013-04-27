package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYao;
import icbm.zhapin.zhapin.EZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ExFuLan extends ExThr
{
	public ExFuLan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		super.baoZhaQian(worldObj, position, explosionSource);

		if (!worldObj.isRemote)
		{
			ThrSheXian thread = new ThrSheXian(worldObj, position, (int) ZhaPin.yuanZi.getRadius(), 200, explosionSource);
			thread.run();
			((EZhaPin) explosionSource).dataList1.add(thread);
		}
	}

	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		super.baoZhaHou(worldObj, position, explosionSource);

		EZhaPin source = (EZhaPin) explosionSource;

		if (!worldObj.isRemote && source.dataList1.size() > 0 && source.dataList1.get(0) instanceof ThrSheXian)
		{
			ThrSheXian thread = (ThrSheXian) source.dataList1.get(0);

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
