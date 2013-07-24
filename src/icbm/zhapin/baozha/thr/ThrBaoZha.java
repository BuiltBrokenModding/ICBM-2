package icbm.zhapin.baozha.thr;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

/**
 * Used for small explosions.
 * 
 * @author Calclavia
 * 
 */
public class ThrBaoZha extends ThrEx
{
	public ThrBaoZha(World world, Vector3 position, int banJing, Entity source)
	{
		super(world, position, banJing, 0, source);
	}

	@Override
	public void run()
	{
		if (!this.world.isRemote)
		{
			for (int x = 0; x < this.banJing; ++x)
			{
				for (int y = 0; y < this.banJing; ++y)
				{
					for (int z = 0; z < this.banJing; ++z)
					{
						if (x == 0 || x == this.banJing - 1 || y == 0 || y == this.banJing - 1 || z == 0 || z == this.banJing - 1)
						{
							double xStep = x / (this.banJing - 1.0F) * 2.0F - 1.0F;
							double yStep = y / (this.banJing - 1.0F) * 2.0F - 1.0F;
							double zStep = z / (this.banJing - 1.0F) * 2.0F - 1.0F;
							double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
							xStep /= diagonalDistance;
							yStep /= diagonalDistance;
							zStep /= diagonalDistance;
							float power = this.banJing * (0.7F + this.world.rand.nextFloat() * 0.6F);
							double var15 = position.x;
							double var17 = position.y;
							double var19 = position.z;

							for (float var21 = 0.3F; power > 0.0F; power -= var21 * 0.75F)
							{
								Vector3 targetPosition = new Vector3(var15, var17, var19);
								double distanceFromCenter = position.distanceTo(targetPosition);
								int blockID = this.world.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

								if (blockID > 0)
								{
									float resistance = 0;

									if (blockID == Block.bedrock.blockID)
									{
										break;
									}
									else
									{
										resistance = Block.blocksList[blockID].getExplosionResistance(this.source, this.world, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ());
									}

									power -= resistance;
								}

								if (power > 0.0F)
								{
									this.results.add(targetPosition.clone());
								}

								var15 += xStep * var21;
								var17 += yStep * var21;
								var19 += zStep * var21;
							}
						}
					}
				}
			}
		}

		super.run();
	}
}
