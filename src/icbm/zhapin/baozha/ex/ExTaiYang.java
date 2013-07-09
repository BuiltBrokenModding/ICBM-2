package icbm.zhapin.baozha.ex;

import icbm.core.ZhuYaoICBM;
import icbm.zhapin.EFeiBlock;
import icbm.zhapin.EGuang;
import icbm.zhapin.baozha.EBaoZha;
import icbm.zhapin.zhapin.ZhaPin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExTaiYang extends ZhaPin
{
	public boolean createNetherrack = true;

	public ExTaiYang(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.createNetherrack = ZhuYaoICBM.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Exothermic Create Netherrack", createNetherrack).getBoolean(createNetherrack);
	}

	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
		super.onYinZha(worldObj, position, fuseTicks);
		worldObj.spawnParticle("lava", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "!!!", "!@!", "!!!", '@', Block.glass, '!', ZhaPin.huo.getItemStack() }), this.getUnlocalizedName(), ZhuYaoICBM.CONFIGURATION, true);
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzTaiYang(world, entity, x, y, z, 50).explode();
	}
}
