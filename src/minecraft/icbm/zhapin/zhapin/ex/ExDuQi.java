package icbm.zhapin.zhapin.ex;

import icbm.api.ICBM;
import icbm.core.ZhuYao;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.EShouLiuDan;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExDuQi extends ZhaPin
{
	public ExDuQi(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		int duration = (3 * 20) / this.proceduralInterval();

		boolean isContagious = this.getTier() == 2;
		float radius = this.getRadius();

		if (explosionSource instanceof EShouLiuDan)
		{
			radius /= 2;
		}

		if (worldObj.isRemote)
		{
			float red = 0.8f;
			float green = 0.8f;
			float blue = 0;

			if (isContagious)
			{
				red = 0.3f;
				green = 0.8f;
				blue = 0;
			}

			for (int i = 0; i < 200; i++)
			{
				Vector3 diDian = new Vector3();

				diDian.x = Math.random() * this.getRadius() / 2 - this.getRadius() / 4;
				diDian.y = Math.random() * this.getRadius() / 2 - this.getRadius() / 4;
				diDian.z = Math.random() * this.getRadius() / 2 - this.getRadius() / 4;
				diDian.multiply(Math.min(this.getRadius(), callCount) / 10);

				if (diDian.getMagnitude() <= this.getRadius())
				{
					diDian.add(new Vector3(explosionSource));

					ZhuYaoZhaPin.proxy.spawnParticle("smoke", explosionSource.worldObj, diDian, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2, red, green, blue, 4f, 8);
				}
			}
		}

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
		List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);

		for (EntityLiving entity : entitiesNearby)
		{
			if (isContagious)
			{
				ZhuYaoZhaPin.DU_CHUAN_RAN.poisonEntity(position, entity);
			}
			else
			{
				ZhuYaoZhaPin.DU_DU.poisonEntity(position, entity);
			}
		}

		worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, "icbm.gasleak", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);

		if (isContagious)
		{
			ZhaPin.bianZhong.doBaoZha(worldObj, position, null, (int) radius, -1);
		}

		if (callCount > duration)
		{
			return false;
		}

		return true;
	}

	@Override
	public int proceduralInterval()
	{
		return 5;
	}

	@Override
	public void init()
	{
		if (this.getTier() == 1)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', ZhuYao.itDu, '?', qi.getItemStack() }), "Chemical", ICBM.CONFIGURATION, true);
		}
		else if (this.getTier() == 2)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { " @ ", "@?@", " @ ", '?', Item.rottenFlesh, '@', duQi.getItemStack() }), "Contagious", ICBM.CONFIGURATION, true);
		}
	}

	@Override
	public float getRadius()
	{
		return this.getTier() == 2 ? 22 : 15;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}

}
