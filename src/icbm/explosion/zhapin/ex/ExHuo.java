package icbm.explosion.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.base.MICBM;
import icbm.explosion.baozha.bz.BzHuo;
import icbm.explosion.muoxing.daodan.MMHuo;
import icbm.explosion.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExHuo extends DaoDan
{
	public ExHuo(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@!@", '@', "dustSulfur", '?', tui.getItemStack(), '!', Item.bucketLava }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
		super.onYinZha(worldObj, position, fuseTicks);
		worldObj.spawnParticle("lava", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzHuo(world, entity, x, y, z, 14).explode();
	}

	@SideOnly(Side.CLIENT)
	public MICBM getMuoXing()
	{
		return new MMHuo();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public MICBM getMissileModel()
	{
		return new MMHuo();
	}
}
