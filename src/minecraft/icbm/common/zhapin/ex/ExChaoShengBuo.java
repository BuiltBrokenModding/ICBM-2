package icbm.common.zhapin.ex;

import icbm.common.EFeiBlock;
import icbm.common.ZhuYao;
import icbm.common.daodan.EDaoDan;
import icbm.common.zhapin.BZhaDan;
import icbm.common.zhapin.EZhaPin;
import icbm.common.zhapin.TZhaDan;
import icbm.common.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import chb.mods.mffs.api.IForceFieldBlock;

public class ExChaoShengBuo extends ZhaPin
{
	private static final int BAN_JING = 15;
	private static final int NENG_LIANG = 80;

	public ExChaoShengBuo(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		if (!worldObj.isRemote)
		{
			EZhaPin source = (EZhaPin) explosionSource;

			for (int x = -BAN_JING * 2; x < BAN_JING * 2; ++x)
			{
				for (int y = -BAN_JING * 2; y < BAN_JING * 2; ++y)
				{
					for (int z = -BAN_JING * 2; z < BAN_JING * 2; ++z)
					{
						Vector3 targetPosition = Vector3.add(position, new Vector3(x, y, z));
						int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

						if (blockID > 0)
						{
							Material material = worldObj.getBlockMaterial(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

							if (!(Block.blocksList[blockID] instanceof BlockFluid) && (Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ()) > NENG_LIANG || material == Material.glass))
							{
								targetPosition.setBlockWithNotify(worldObj, 0);
							}
						}
					}
				}
			}

			for (int x = 0; x < BAN_JING; ++x)
			{
				for (int y = 0; y < BAN_JING; ++y)
				{
					for (int z = 0; z < BAN_JING; ++z)
					{
						if (x == 0 || x == BAN_JING - 1 || y == 0 || y == BAN_JING - 1 || z == 0 || z == BAN_JING - 1)
						{
							double xStep = (double) ((float) x / ((float) BAN_JING - 1.0F) * 2.0F - 1.0F);
							double yStep = (double) ((float) y / ((float) BAN_JING - 1.0F) * 2.0F - 1.0F);
							double zStep = (double) ((float) z / ((float) BAN_JING - 1.0F) * 2.0F - 1.0F);
							double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
							xStep /= diagonalDistance;
							yStep /= diagonalDistance;
							zStep /= diagonalDistance;
							float power = NENG_LIANG * (0.7F + worldObj.rand.nextFloat() * 0.6F);
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
										resistance = 2f;
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

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.hypersonic", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.1F) * 0.9F);
	}

	// Sonic Explosion is a procedural explosive
	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		EZhaPin source = (EZhaPin) explosionSource;

		int r = callCount;

		if (!worldObj.isRemote)
		{
			for (Object obj : source.dataList)
			{
				Vector3 targetPosition = (Vector3) obj;

				double distance = Vector3.distance(targetPosition, position);

				if (distance > r || distance < r - 3)
					continue;

				int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

				if (blockID == 0 || blockID == Block.bedrock.blockID || blockID == Block.obsidian.blockID)
					continue;

				if (Block.blocksList[blockID] instanceof IForceFieldBlock)
					continue;

				int metadata = worldObj.getBlockMetadata(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

				if (distance < r - 1 || worldObj.rand.nextInt(3) > 0)
				{
					if (blockID == ZhuYao.bZhaDan.blockID)
					{
						BZhaDan.yinZha(worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), ((TZhaDan) worldObj.getBlockTileEntity(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ())).explosiveID, 1);
					}
					else
					{
						worldObj.setBlockWithNotify(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), 0);
					}

					targetPosition.add(0.5D);

					if (worldObj.rand.nextFloat() < 0.2 * (BAN_JING - r))
					{
						EFeiBlock entity = new EFeiBlock(worldObj, targetPosition, blockID, metadata);
						worldObj.spawnEntityInWorld(entity);
						entity.yawChange = 50 * worldObj.rand.nextFloat();
						entity.pitchChange = 100 * worldObj.rand.nextFloat();
					}
				}
			}
		}

		int radius = 2 * callCount;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
		List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

		for (int var11 = 0; var11 < allEntities.size(); ++var11)
		{
			Entity entity = (Entity) allEntities.get(var11);

			if (entity instanceof EDaoDan)
			{
				((EDaoDan) entity).explode();
				break;
			}
			else
			{
				double xDifference = entity.posX - position.x;
				double zDifference = entity.posZ - position.z;

				r = (int) BAN_JING;
				if (xDifference < 0)
					r = (int) -BAN_JING;

				entity.motionX += (r - xDifference) * 0.02 * worldObj.rand.nextFloat();
				entity.motionY += 4 * worldObj.rand.nextFloat();

				r = (int) BAN_JING;
				if (zDifference < 0)
					r = (int) -BAN_JING;

				entity.motionZ += (r - zDifference) * 0.02 * worldObj.rand.nextFloat();
			}
		}

		if (callCount > BAN_JING) { return false; }

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
		return 4;
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " S ", "SSS", " S ", 'S', ZhaPin.shengBuo.getItemStack() }), this.getMing(), ZhuYao.CONFIGURATION, true);
	}
}
