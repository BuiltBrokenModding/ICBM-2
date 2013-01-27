package icbm.common.zhapin;

import icbm.common.ZhuYao;

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
	public float getRadius()
	{
		return this.getTier() == 2 ? 20 : 14;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}

	/**
	 * World worldObj, Vector3 position, int radius, boolean isContagious
	 */
	@Override
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource)
	{
		boolean isContagious = this.getTier() == 2;
		int radius = (int) this.getRadius();

		if (explosionSource instanceof EShouLiuDan)
		{
			radius /= 2;
		}

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
		List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);

		for (EntityLiving entity : entitiesNearby)
		{
			if (isContagious)
			{
				ZhuYao.DU_CHUAN_RAN.poisonEntity(entity);
			}
			else
			{
				ZhuYao.DU_DU.poisonEntity(entity);
			}
		}

		worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, "icbm.gasleak", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);

		if (isContagious)
		{
			ZhaPin.bianZhong.doBaoZha(worldObj, position, null, radius, -1);
		}
	}

	@Override
	public void init()
	{
		if (this.getTier() == 1)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', ZhuYao.itDu, '?', tui.getItemStack() }), "Chemical", ZhuYao.CONFIGURATION, true);
		}
		else if (this.getTier() == 2)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(2), new Object[] { " @ ", "@?@", " @ ", '?', Item.rottenFlesh, '@', duQi.getItemStack() }), "Contagious", ZhuYao.CONFIGURATION, true);
		}
	}
}
