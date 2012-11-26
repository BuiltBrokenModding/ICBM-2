package icbm.zhapin.ex;

import icbm.ParticleSpawner;
import icbm.ZhuYao;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFluid;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExYuanZi extends ZhaPin
{
	public static final int BAN_JING = 30;
	public static final int NENG_LIANG = 200;
	public static final int CALC_SPEED = 500;

	public ExYuanZi(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(200);
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		EZhaPin source = (EZhaPin) explosionSource;

		int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / BAN_JING));

		for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
		{
			for (int theta_n = 0; theta_n < steps; theta_n++)
			{
				double phi = Math.PI * 2 / steps * phi_n;
				double theta = Math.PI / steps * theta_n;

				source.dataList.add(new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi)));
			}
		}

		doDamageEntities(worldObj, position, BAN_JING, NENG_LIANG * 1000);

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		EZhaPin source = (EZhaPin) explosionSource;

		int i = (callCount + 1) * CALC_SPEED - CALC_SPEED;

		if (!worldObj.isRemote)
		{
			for (; i < source.dataList.size(); i++)
			{
				if (i > (callCount + 1) * CALC_SPEED)
					break;

				Vector3 delta = (Vector3) source.dataList.get(i);

				float power = NENG_LIANG - (NENG_LIANG * worldObj.rand.nextFloat() / 2);

				Vector3 targetPosition = position.clone();

				for (float var21 = 0.3F; power > 0f; power -= var21 * 0.75F * 10)
				{
					if (targetPosition.distanceTo(position) > BAN_JING)
						break;

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
						else if (blockID == Block.obsidian.blockID)
						{
							resistance = 80f;
						}
						else
						{
							resistance = Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ()) * 4;
						}

						power -= resistance;

						if (power > 0f)
						{
							if (!source.dataList2.contains(targetPosition))
							{
								source.dataList2.add(targetPosition.clone());
							}
						}
					}

					targetPosition.x += delta.x;
					targetPosition.y += delta.y;
					targetPosition.z += delta.z;
				}
			}
		}

		if (worldObj.isRemote)
		{
			int r = (int) (callCount / 2);

			boolean reverse = false;

			if (r > BAN_JING)
			{
				r = BAN_JING - (r - BAN_JING);
				reverse = true;
			}

			if (r > 0 && ZhuYao.SPAWN_PARTICLES)
			{
				for (int x = -r; x < r; x++)
				{
					for (int z = -r; z < r; z++)
					{
						double distance = MathHelper.sqrt_double(x * x + z * z);

						if (distance < r && distance > r - 1)
						{
							Vector3 targetPosition = Vector3.add(position, new Vector3(x, 0, z));

							if (!reverse)
							{
								if (worldObj.rand.nextFloat() < Math.max(0.0006 * r, 0.005) || (ZhuYao.ADVANCED_VISUALS && worldObj.rand.nextFloat() < Math.max(0.0008 * r, 0.008)))
								{
									worldObj.spawnParticle("hugeexplosion", targetPosition.x, targetPosition.y, targetPosition.z, 0, 0, 0);
								}
							}
							else
							{
								if (worldObj.rand.nextFloat() < Math.max(0.001 * r, 0.05))
								{
									ParticleSpawner.spawnParticle("smoke", worldObj, targetPosition, 0F, 0F, 0F, 5F, 1F);
								}
							}
						}
					}
				}
			}

			if (r <= 0 && i > source.dataList.size())
				return false;
		}
		else if (i > source.dataList.size()) { return false; }

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.redmatter", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		return true;
	}

	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		EZhaPin source = (EZhaPin) explosionSource;

		if (!worldObj.isRemote)
		{
			for (Object obj : source.dataList2)
			{
				Vector3 targetPosition = (Vector3) obj;
				int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

				if (blockID > 0)
				{
					worldObj.setBlockWithNotify(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), 0);
					Block.blocksList[blockID].onBlockDestroyedByExplosion(worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
				}
			}
		}

		doDamageEntities(worldObj, position, BAN_JING, NENG_LIANG * 1000);

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 10.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		ZhaPin.fuLan.doBaoZha(worldObj, position, null, BAN_JING + 15, -1);
		ZhaPin.bianZhong.doBaoZha(worldObj, position, null, BAN_JING + 20, -1);

		if (worldObj.isRemote && ZhuYao.ADVANCED_VISUALS)
		{
			for (int y = 0; y < 25; y++)
			{
				int r = 3;

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
							ParticleSpawner.spawnParticle("smoke", worldObj, Vector3.add(new Vector3(x * 2, (y - 2) * 2, z * 2), position), 0F, 0F, 0F, 10F, 1F);
						}
					}
				}
			}
		}
	}

	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * 
	 * @param return - Return -1 if this explosive does not need procedural calls
	 */
	@Override
	public int proceduralInterval()
	{
		return 1;
	}

	/**
	 * Called when the explosive is on fuse and going to explode. Called only when the explosive is
	 * in it's TNT form.
	 * 
	 * @param fuseTicks
	 *            - The amount of ticks this explosive is on fuse
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
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[]
		{ "?@?", "@!@", "?@?", '!', yaSuo.getItemStack(), '@', Block.tnt, '?', "ingotUranium" }), this.getMing(), ZhuYao.CONFIGURATION, true);
	}
}
