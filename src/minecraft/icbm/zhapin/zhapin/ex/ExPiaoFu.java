package icbm.zhapin.zhapin.ex;

import icbm.api.ICBM;
import icbm.zhapin.EFeiBlock;
import icbm.zhapin.zhapin.EZhaPin;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.List;

import mffs.api.IForceFieldBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExPiaoFu extends ZhaPin
{
	public ExPiaoFu(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		super.baoZhaQian(worldObj, position, explosionSource);

		if (!worldObj.isRemote)
		{
			EZhaPin source = (EZhaPin) explosionSource;

			for (int x = 0; x < this.getRadius(); ++x)
			{
				for (int y = 0; y < this.getRadius(); ++y)
				{
					for (int z = 0; z < this.getRadius(); ++z)
					{
						if (x == 0 || x == this.getRadius() - 1 || y == 0 || y == this.getRadius() - 1 || z == 0 || z == this.getRadius() - 1)
						{
							double xStep = (double) ((float) x / ((float) this.getRadius() - 1.0F) * 2.0F - 1.0F);
							double yStep = (double) ((float) y / ((float) this.getRadius() - 1.0F) * 2.0F - 1.0F);
							double zStep = (double) ((float) z / ((float) this.getRadius() - 1.0F) * 2.0F - 1.0F);
							double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
							xStep /= diagonalDistance;
							yStep /= diagonalDistance;
							zStep /= diagonalDistance;
							float power = this.getRadius() * (0.7F + worldObj.rand.nextFloat() * 0.6F);
							double var15 = position.x;
							double var17 = position.y;
							double var19 = position.z;

							for (float var21 = 0.3F; power > 0.0F; power -= var21 * 0.75F)
							{
								Vector3 targetPosition = new Vector3(var15, var17, var19);
								double distanceFromCenter = position.distanceTo(targetPosition);
								int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

								if (blockID > 0)
								{
									float resistance = 0;

									if (blockID == Block.bedrock.blockID)
									{
										break;
									}
									else if (Block.blocksList[blockID] instanceof BlockFluid)
									{
										resistance = 1f;
									}
									else
									{
										resistance = (Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ()) + 0.3F) * var21;
									}

									power -= resistance;
								}

								if (power > 0.0F)
								{
									source.dataList.add(targetPosition.clone());
								}

								var15 += xStep * (double) var21;
								var17 += yStep * (double) var21;
								var19 += zStep * (double) var21;
							}
						}
					}
				}
			}
		}

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.antigravity", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		EZhaPin source = (EZhaPin) explosionSource;

		int r = callCount;

		if (!worldObj.isRemote)
		{
			int blocksToTake = 20;

			for (Object obj : source.dataList)
			{
				Vector3 targetPosition = (Vector3) obj;

				double distance = Vector3.distance(targetPosition, position);

				if (distance > r || distance < r - 2 || blocksToTake <= 0)
					continue;

				int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

				if (blockID == 0 || blockID == Block.bedrock.blockID || blockID == Block.obsidian.blockID)
					continue;

				if (Block.blocksList[blockID] instanceof IForceFieldBlock)
					continue;

				int metadata = worldObj.getBlockMetadata(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

				if (distance < r - 1 || worldObj.rand.nextInt(3) > 0)
				{
					worldObj.setBlockAndMetadataWithNotify(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), 0, 0, 2);

					targetPosition.add(0.5D);

					if (worldObj.rand.nextFloat() < 0.3 * (this.getRadius() - r))
					{
						EFeiBlock entity = new EFeiBlock(worldObj, targetPosition, blockID, metadata, 0);
						worldObj.spawnEntityInWorld(entity);
						entity.yawChange = 50 * worldObj.rand.nextFloat();
						entity.pitchChange = 100 * worldObj.rand.nextFloat();
						entity.motionY += Math.max(0.15 * worldObj.rand.nextFloat(), 0.1);
					}

					blocksToTake--;
				}
			}
		}

		int radius = (int) this.getRadius();
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, 100, position.z + radius);
		List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

		for (Entity entity : allEntities)
		{
			if (!(entity instanceof EFeiBlock) && entity.posY < 100 + position.y)
			{
				if (entity.motionY < 0.4)
				{
					entity.motionY += 0.15;
				}
			}
		}

		if (callCount > 20 * 120)
		{
			return false;
		}

		return true;
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
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "EEE", "ETE", "EEE", 'T', tui.getItemStack(), 'E', Item.eyeOfEnder }), this.getName(), ICBM.CONFIGURATION, true);
	}

	@Override
	public float getRadius()
	{
		return 15;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}
}
