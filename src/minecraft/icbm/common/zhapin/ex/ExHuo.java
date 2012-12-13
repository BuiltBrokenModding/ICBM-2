package icbm.common.zhapin.ex;

import icbm.common.ZhuYao;
import icbm.common.zhapin.EShouLiuDan;
import icbm.common.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExHuo extends ZhaPin
{
	public ExHuo(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	/**
	 * World worldObj, Vector3 position, int amount, boolean isExplosive
	 */
	@Override
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource)
	{
		int radius = 14;

		if (explosionSource instanceof EShouLiuDan)
		{
			radius /= 2;
		}

		for (int x = 0; x < radius; ++x)
		{
			for (int y = 0; y < radius; ++y)
			{
				for (int z = 0; z < radius; ++z)
				{
					if (x == 0 || x == radius - 1 || y == 0 || y == radius - 1 || z == 0 || z == radius - 1)
					{
						double xStep = (double) ((float) x / ((float) radius - 1.0F) * 2.0F - 1.0F);
						double yStep = (double) ((float) y / ((float) radius - 1.0F) * 2.0F - 1.0F);
						double zStep = (double) ((float) z / ((float) radius - 1.0F) * 2.0F - 1.0F);
						double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
						xStep /= diagonalDistance;
						yStep /= diagonalDistance;
						zStep /= diagonalDistance;
						float var14 = radius * (0.7F + worldObj.rand.nextFloat() * 0.6F);
						double var15 = position.x;
						double var17 = position.y;
						double var19 = position.z;

						for (float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F)
						{
							Vector3 targetPosition = new Vector3(var15, var17, var19);
							double distanceFromCenter = position.distanceTo(targetPosition);
							int var25 = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

							if (var25 > 0)
							{
								var14 -= (Block.blocksList[var25].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ()) + 0.3F) * var21;
							}

							if (var14 > 0.0F)
							{
								// Set fire by
								// chance and
								// distance
								double chance = radius - (Math.random() * distanceFromCenter);

								// System.out.println("Distance: "+distance+", "+chance);
								if (chance > distanceFromCenter * 0.55)
								{
									// Check to
									// see if the
									// block is an
									// air block
									// and there
									// is a block
									// below it to
									// support the
									// fire
									int blockID = worldObj.getBlockId((int) targetPosition.x, (int) targetPosition.y, (int) targetPosition.z);

									if ((blockID == 0 || blockID == Block.snow.blockID) && worldObj.getBlockMaterial((int) targetPosition.x, (int) targetPosition.y - 1, (int) targetPosition.z).isSolid())
									{
										worldObj.setBlockWithNotify((int) targetPosition.x, (int) targetPosition.y, (int) targetPosition.z, Block.fire.blockID);
									}
									else if (blockID == Block.ice.blockID)
									{
										worldObj.setBlockWithNotify((int) targetPosition.x, (int) targetPosition.y, (int) targetPosition.z, 0);
									}
								}
							}

							var15 += xStep * (double) var21;
							var17 += yStep * (double) var21;
							var19 += zStep * (double) var21;
						}
					}
				}
			}
		}

		worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, "icbm.explosionfire", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@!@", '@', ZhuYao.itLiu, '?', Block.tnt, '!', Item.bucketLava }), this.getMing(), ZhuYao.CONFIGURATION, true);
	}

}
