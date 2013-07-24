package icbm.zhapin.zhapin.ex;

import icbm.core.MICBM;
import icbm.core.SheDing;
import icbm.zhapin.baozha.bz.BzTaiYang;
import icbm.zhapin.muoxing.daodan.MMTaiYang;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExTaiYang extends DaoDan
{
	public boolean createNetherrack = true;

	public ExTaiYang(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.createNetherrack = SheDing.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Exothermic Create Netherrack", createNetherrack).getBoolean(createNetherrack);
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
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "!!!", "!@!", "!!!", '@', Block.glass, '!', ZhaPin.huo.getItemStack() }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzTaiYang(world, entity, x, y, z, 50).explode();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MICBM getMissileModel()
	{
		return new MMTaiYang();
	}
}
