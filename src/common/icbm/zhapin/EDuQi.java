package icbm.zhapin;

import icbm.ZhuYao;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class EDuQi extends ZhaPin
{
	public EDuQi(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	/**
	 * World worldObj, Vector3 position, int radius, boolean isContagious
	 */
	@Override
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource)
	{
		boolean isContagious = false;
		int radius = 14;

		if (this.getTier() == 2)
		{
			isContagious = true;
			radius = 20;
		}

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
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', ZhuYao.itDu, '?', Block.tnt }), "Chemical", ZhuYao.CONFIGURATION, true);
		}
		else if (this.getTier() == 2)
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { " @ ", "@?@", " @ ", '?', Item.rottenFlesh, '@', duQi.getItemStack() }), "Contagious", ZhuYao.CONFIGURATION, true);
		}
	}
}
