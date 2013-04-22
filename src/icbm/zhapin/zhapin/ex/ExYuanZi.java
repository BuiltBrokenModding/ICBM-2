package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYao;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.ex.ThrSheXian.IThreadCallBack;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExYuanZi extends ZhaPin implements IThreadCallBack
{
	public static final int BAN_JING = 45;
	public static final int NENG_LIANG = 200;

	public ExYuanZi(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(200);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		super.doBaoZha(worldObj, position, explosionSource);

		if (!worldObj.isRemote)
		{
			new ThrSheXian(worldObj, position, BAN_JING, NENG_LIANG, this, explosionSource).run();
		}
		else if (ZhuYaoZhaPin.proxy.isGaoQing())
		{
			// Spawn nuclear cloud.
			for (int y = 0; y < 26; y++)
			{
				int r = 4;

				if (y < 8)
				{
					r = Math.max(Math.min((8 - y) * 2, 10), 4);
				}
				else if (y > 15)
				{
					r = Math.max(Math.min((y - 15) * 2, 15), 5);
				}

				for (int x = -r; x < r; x++)
				{
					for (int z = -r; z < r; z++)
					{
						double distance = MathHelper.sqrt_double(x * x + z * z);

						if (r > distance && r - 3 < distance)
						{
							Vector3 spawnPosition = Vector3.add(position, new Vector3(x * 2, (y - 2) * 2, z * 2));
							float xDiff = (float) (spawnPosition.x - position.x);
							float zDiff = (float) (spawnPosition.z - position.z);

							ZhuYaoZhaPin.proxy.spawnParticle("smoke", worldObj, spawnPosition, xDiff * 0.3 * worldObj.rand.nextFloat(), -worldObj.rand.nextFloat(), zDiff * 0.3 * worldObj.rand.nextFloat(), (float) (distance / BAN_JING) * worldObj.rand.nextFloat(), 0, 0, 8F, 1.2F);
						}
					}
				}
			}
		}

		this.doDamageEntities(worldObj, position, BAN_JING, NENG_LIANG * 1000);

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

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
				int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

				if (blockID > 0)
				{
					try
					{
						targetPosition.setBlock(worldObj, 0);
						Block.blocksList[blockID].onBlockDestroyedByExplosion(thread.world, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), null);
					}
					catch (Exception e)
					{
						ZhuYao.LOGGER.severe("Detonation Failed!");
						e.printStackTrace();
					}
				}
			}
		}

		this.doDamageEntities(worldObj, position, BAN_JING, NENG_LIANG * 1000);

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 10.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		ZhaPin.fuLan.doBaoZha(worldObj, position, null, BAN_JING + 15, -1);
		ZhaPin.bianZhong.doBaoZha(worldObj, position, null, BAN_JING + 20, -1);
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
			worldObj.playSoundEffect((int) position.x, (int) position.y, (int) position.z, "icbm.alarm", 4F, 1F);
		}
	}

	@Override
	public void init()
	{
		if (OreDictionary.getOres("ingotUranium").size() > 0)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "UUU", "UEU", "UUU", 'E', wenYa.getItemStack(), 'U', "ingotUranium" }), this.getUnlocalizedName(), ZhuYao.CONFIGURATION, true);
		}
		else
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "EEE", "EEE", "EEE", 'E', wenYa.getItemStack() }), this.getUnlocalizedName(), ZhuYao.CONFIGURATION, true);

		}
	}

	@Override
	public float getRadius()
	{
		return BAN_JING;
	}

	@Override
	public double getEnergy()
	{
		return 100000;
	}

}
