package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYao;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ExFuLan extends ZhaPin
{
	public ExFuLan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		if (!worldObj.isRemote)
		{
			for (int x = -radius; x < radius; x++)
			{
				for (int y = -radius; y < radius; y++)
				{
					for (int z = -radius; z < radius; z++)
					{
						double dist = MathHelper.sqrt_double((x * x + y * y + z * z));

						if (dist > radius)
							continue;

						Vector3 blockPosition = new Vector3(x, y, z);
						blockPosition.add(position);

						/**
						 * Decay the blocks.
						 */
						int blockID = worldObj.getBlockId((int) blockPosition.x, (int) blockPosition.y, (int) blockPosition.z);

						if (blockID == Block.grass.blockID || blockID == Block.sand.blockID)
						{
							if (worldObj.rand.nextFloat() > 0.92)
							{
								worldObj.setBlock((int) blockPosition.x, (int) blockPosition.y, (int) blockPosition.z, ZhuYao.bFuShe.blockID, 0, 2);
							}
						}

						if (blockID == Block.stone.blockID)
						{
							if (worldObj.rand.nextFloat() > 0.97)
							{
								worldObj.setBlock((int) blockPosition.x, (int) blockPosition.y, (int) blockPosition.z, ZhuYao.bFuShe.blockID, 0, 2);
							}
						}

						else if (blockID == Block.leaves.blockID)
						{
							worldObj.setBlock((int) blockPosition.x, (int) blockPosition.y, (int) blockPosition.z, 0, 0, 2);
						}
						else if (blockID == Block.tallGrass.blockID)
						{
							if (Math.random() * 100 > 50)
								worldObj.setBlock((int) blockPosition.x, (int) blockPosition.y, (int) blockPosition.z, Block.cobblestone.blockID, 0, 2);
							else
								worldObj.setBlock((int) blockPosition.x, (int) blockPosition.y, (int) blockPosition.z, 0, 0, 2);
						}
						else if (blockID == Block.tilledField.blockID)
						{
							worldObj.setBlock((int) blockPosition.x, (int) blockPosition.y, (int) blockPosition.z, ZhuYao.bFuShe.blockID, 0, 2);
						}
					}
				}
			}
		}

		return false;
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
