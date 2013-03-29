package icbm.zhapin.zhapin.ex;

import icbm.api.ICBM;
import icbm.zhapin.fx.FXYan;
import icbm.zhapin.zhapin.EShouLiuDan;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.potion.CustomPotionEffect;

public class ExQi extends ZhaPin
{
	public ExQi(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.debilitation", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		float radius = this.getRadius();

		if (explosionSource instanceof EShouLiuDan)
		{
			radius /= 2;
		}
		int duration = 20 * 30;

		if (worldObj.isRemote)
		{
			for (int i = 0; i < 200; i++)
			{
				Vector3 diDian = new Vector3();

				diDian.x = Math.random() * radius / 2 - radius / 4;
				diDian.y = Math.random() * radius / 2 - radius / 4;
				diDian.z = Math.random() * radius / 2 - radius / 4;
				diDian.multiply(Math.min(radius, callCount) / 10);

				if (diDian.getMagnitude() <= radius)
				{
					diDian.add(new Vector3(explosionSource));
					FXYan fx = new FXYan(explosionSource.worldObj, diDian, 1, 1, 1, 7.0F, 8);
					fx.motionX = (Math.random() - 0.5) / 2;
					fx.motionY = (Math.random() - 0.5) / 2;
					fx.motionZ = (Math.random() - 0.5) / 2;
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
		List<EntityLiving> allEntities = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);

		for (EntityLiving entity : allEntities)
		{
			entity.addPotionEffect(new CustomPotionEffect(Potion.confusion.id, 18 * 20, 0));
			entity.addPotionEffect(new CustomPotionEffect(Potion.digSlowdown.id, 20 * 60, 0));
			entity.addPotionEffect(new CustomPotionEffect(Potion.moveSlowdown.id, 20 * 60, 2));
		}

		if (callCount > duration)
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
		return 5;
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(3), new Object[] { "SSS", "WRW", "SSS", 'R', ZhaPin.tui.getItemStack(), 'W', Item.bucketWater, 'S', "sulfur" }), this.getUnlocalizedName(), ICBM.CONFIGURATION, true);
	}

	@Override
	public float getRadius()
	{
		return 20;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}
}
