package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYao;
import icbm.zhapin.zhapin.EZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExFanWuSu extends ZhaPin
{
	public boolean destroyBedrock = true;

	public ExFanWuSu(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(300);
		ZhuYao.CONFIGURATION.load();
		this.destroyBedrock = ZhuYao.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Antimatter Destroy Bedrock", this.destroyBedrock).getBoolean(this.destroyBedrock);
		ZhuYao.CONFIGURATION.save();
	}

	/**
	 * Called before an explosion happens
	 */
	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		super.baoZhaQian(worldObj, position, explosionSource);
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.antimatter", 7F, (float) (worldObj.rand.nextFloat() * 0.1 + 0.9F));
		explosionSource.posY += 5;
		doDamageEntities(worldObj, position, this.getRadius() * 2, Integer.MAX_VALUE);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		if (!worldObj.isRemote)
		{
			if (callCount == 1)
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
									if (blockID == Block.bedrock.blockID && !destroyBedrock)
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

				return false;
			}
		}
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

		if (callCount > this.getRadius())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		super.baoZhaHou(worldObj, position, explosionSource);

		doDamageEntities(worldObj, position, this.getRadius() * 2, Integer.MAX_VALUE);
	}

	@Override
	protected boolean onDamageEntity(Entity entity)
	{
		if (entity instanceof EZhaPin)
		{
			if (((EZhaPin) entity).haoMa == ZhaPin.hongSu.getID())
			{
				entity.setDead();
				return true;
			}
		}

		return false;
	}

	/**
	 * Called when the explosive is on fuse and going to explode. Called only when the explosive is
	 * in it's TNT form.
	 * 
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
		super.onYinZha(worldObj, position, fuseTicks);

		if (fuseTicks % 25 == 0)
		{
			worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.alarm", 4F, 1F);
		}
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', ZhaPin.yuanZi.getItemStack(), 'A', "antimatterGram" }), this.getUnlocalizedName(), ZhuYao.CONFIGURATION, true);
	}

	@Override
	protected int proceduralInterval()
	{
		return 1;
	}

	@Override
	public float getRadius()
	{
		return 40;
	}

	@Override
	public double getEnergy()
	{
		return 1000000;
	}
}
