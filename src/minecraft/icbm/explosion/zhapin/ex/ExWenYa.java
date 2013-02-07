package icbm.explosion.zhapin.ex;

import icbm.api.ICBM;
import icbm.explosion.ZhuYao;
import icbm.explosion.fx.ParticleSpawner;
import icbm.explosion.zhapin.EZhaPin;
import icbm.explosion.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExWenYa extends ZhaPin
{
	public static final int BAN_JING = 20;
	public static final int NENG_LIANG = 150;
	public static final int CALC_SPEED = 800;

	public ExWenYa(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(120);
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		super.baoZhaQian(worldObj, position, explosionSource);

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
							resistance = Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ()) * 5;
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

		int r = callCount;

		boolean reverse = false;

		if (r > BAN_JING)
		{
			r = BAN_JING - (r - BAN_JING);
			reverse = true;
		}

		if (r > 0)
		{
			if (worldObj.isRemote && ZhuYao.proxy.isGaoQing())
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
								if (worldObj.rand.nextFloat() < Math.max(0.0006 * r, 0.005))
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
		}

		if (r <= 0 && i > source.dataList.size()) { return false; }

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.redmatter", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		return true;
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

	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		super.baoZhaHou(worldObj, position, explosionSource);

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
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "CIC", "IRI", "CIC", 'R', ZhaPin.tui.getItemStack(), 'C', ZhaPin.duQi.getItemStack(), 'I', ZhaPin.huo.getItemStack() }), this.getName(), ICBM.CONFIGURATION, true);
	}

	@Override
	public float getRadius()
	{
		return BAN_JING;
	}

	@Override
	public double getEnergy()
	{
		return 80000;
	}
}
