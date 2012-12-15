package icbm.common.zhapin.ex;

import icbm.client.fx.ParticleSpawner;
import icbm.common.ZhuYao;
import icbm.common.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExFanWuSu extends ZhaPin
{
	private static final int BAN_JING = 50;
	public boolean destroyBedrock = false;

	public ExFanWuSu(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(300);
		ZhuYao.CONFIGURATION.load();
		this.destroyBedrock = ZhuYao.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Destroy Bedrock", destroyBedrock).getBoolean(destroyBedrock);
		ZhuYao.CONFIGURATION.save();
	}

	/**
	 * Called before an explosion happens
	 */
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.antimatter", 7F, (float) (worldObj.rand.nextFloat() * 0.1 + 0.9F));
		explosionSource.posY += 5;

		doDamageEntities(worldObj, position, BAN_JING * 2, Integer.MAX_VALUE);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		if (!worldObj.isRemote)
		{
			if (callCount == 1)
			{
				for (int x = -BAN_JING; x < BAN_JING; x++)
				{
					for (int y = -BAN_JING; y < BAN_JING; y++)
					{
						for (int z = -BAN_JING; z < BAN_JING; z++)
						{
							Vector3 targetPosition = Vector3.add(position, new Vector3(x, y, z));
							double dist = position.distanceTo(targetPosition);

							if (dist < BAN_JING)
							{
								int blockID = targetPosition.getBlockID(worldObj);

								if (blockID > 0)
								{
									if (blockID == Block.bedrock.blockID && !destroyBedrock)
										continue;

									if (dist < BAN_JING - 1 || worldObj.rand.nextFloat() > 0.7)
									{
										targetPosition.setBlockWithNotify(worldObj, 0);
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
		 * else if (ZhuYao.proxy.isGaoQing()) { for (int x = -BAN_JING; x < BAN_JING; x++) { for
		 * (int y = -BAN_JING; y < BAN_JING; y++) { for (int z = -BAN_JING; z < BAN_JING; z++) {
		 * Vector3 targetPosition = Vector3.add(position, new Vector3(x, y, z)); double distance =
		 * position.distanceTo(targetPosition);
		 * 
		 * if (targetPosition.getBlockID(worldObj) == 0) { if (distance < BAN_JING && distance >
		 * BAN_JING - 1 && worldObj.rand.nextFloat() > 0.5) {
		 * ParticleSpawner.spawnParticle("antimatter", worldObj, targetPosition); } } } } } }
		 */

		if (callCount > BAN_JING)
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
		doDamageEntities(worldObj, position, BAN_JING * 2, Integer.MAX_VALUE);
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
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', ZhaPin.yuanZi.getItemStack(), 'A', "antimatter" }), this.getMing(), ZhuYao.CONFIGURATION, true);
	}

	@Override
	protected int proceduralInterval()
	{
		return 1;
	}
}
