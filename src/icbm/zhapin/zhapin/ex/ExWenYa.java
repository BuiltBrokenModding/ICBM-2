package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYaoBase;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.EZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExWenYa extends ExThr
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

		if (!worldObj.isRemote)
		{
			ThrSheXian thread = new ThrSheXian(worldObj, position, BAN_JING, NENG_LIANG, explosionSource);
			thread.run();
			((EZhaPin) explosionSource).dataList1.add(thread);
		}

		this.doDamageEntities(worldObj, position, BAN_JING, NENG_LIANG * 1000);

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		int r = callCount;

		if (worldObj.isRemote && ZhuYaoZhaPin.proxy.isGaoQing())
		{
			for (int x = -r; x < r; x++)
			{
				for (int z = -r; z < r; z++)
				{
					double distance = MathHelper.sqrt_double(x * x + z * z);

					if (distance < r && distance > r - 1)
					{
						Vector3 targetPosition = Vector3.add(position, new Vector3(x, 0, z));

						if (worldObj.rand.nextFloat() < Math.max(0.001 * r, 0.05))
						{
							ZhuYaoZhaPin.proxy.spawnParticle("smoke", worldObj, targetPosition, 5F, 1F);
						}
					}
				}
			}
		}

		return super.doBaoZha(worldObj, position, explosionSource, callCount);
	}

	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		super.baoZhaHou(worldObj, position, explosionSource);

		EZhaPin source = (EZhaPin) explosionSource;

		if (!worldObj.isRemote && source.dataList1.size() > 0 && source.dataList1.get(0) instanceof ThrSheXian)
		{
			ThrSheXian thread = (ThrSheXian) source.dataList1.get(0);

			for (Object obj : thread.destroyed)
			{
				Vector3 targetPosition = (Vector3) obj;
				int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

				if (blockID > 0)
				{
					try
					{
						worldObj.setBlock(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), 0, 0, 3);
						Block.blocksList[blockID].onBlockDestroyedByExplosion(worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), null);
					}
					catch (Exception e)
					{
						ZhuYaoBase.LOGGER.severe("Detonation Failed!");
						e.printStackTrace();
					}
				}
			}
		}

		this.doDamageEntities(worldObj, position, BAN_JING, NENG_LIANG * 1000);

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 10.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
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
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "CIC", "IRI", "CIC", 'R', ZhaPin.tui.getItemStack(), 'C', ZhaPin.duQi.getItemStack(), 'I', ZhaPin.huo.getItemStack() }), this.getUnlocalizedName(), ZhuYaoBase.CONFIGURATION, true);
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
