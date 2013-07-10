package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYaoICBM;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzDiLei;
import icbm.zhapin.muoxing.jiqi.MDiLei;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExDiLei extends ZhaPin
{
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation TEXTURE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.MODEL_PATH + "s-mine.png");

	public ExDiLei(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.setYinXin(20);
	}

	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{

	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "S", "L", "R", 'S', ZhaPin.qunDan.getItemStack(), 'L', ZhaPin.la.getItemStack(), 'R', ZhaPin.tui.getItemStack() }), this.getUnlocalizedName(), ZhuYaoICBM.CONFIGURATION, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MICBM getBlockModel()
	{
		return MDiLei.INSTANCE;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getBlockResource()
	{
		return TEXTURE;
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzDiLei(world, entity, x, y, z, 5).explode();
	}

}
