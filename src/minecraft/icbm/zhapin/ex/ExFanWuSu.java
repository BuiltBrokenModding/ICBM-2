package icbm.zhapin.ex;

import icbm.ZhuYao;
import icbm.zhapin.ZhaPin;
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
	private static final int BAN_JING = 28;
	private static final int LAYERS_PER_TICK = 2;
	public boolean destroyBedrock = true;

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
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.antimatter", 5F, 1F);
		explosionSource.posY += 5;

		doDamageEntities(worldObj, position, BAN_JING * 2, Integer.MAX_VALUE);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		if (!worldObj.isRemote)
		{
			while (position.y > 0)
			{
				for (int x = -BAN_JING; x < BAN_JING; x++)
				{
					for (int z = -BAN_JING; z < BAN_JING; z++)
					{
						double dist = MathHelper.sqrt_double((x * x + z * z));

						if (dist > BAN_JING)
							continue;

						Vector3 targetPosition = Vector3.add(new Vector3(x, 0, z), position);
						int blockID = targetPosition.getBlockID(worldObj);

						if (blockID > 0)
						{
							if (blockID == Block.bedrock.blockID && !destroyBedrock)
								continue;

							if (dist < BAN_JING - 1 || worldObj.rand.nextFloat() > 0.7)
							{
								targetPosition.setBlock(worldObj, 0);
							}
						}
					}
				}

				position.y--;
			}
		}

		return false;
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
}
