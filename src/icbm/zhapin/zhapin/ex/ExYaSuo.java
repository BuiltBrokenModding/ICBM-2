package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYaoICBM;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.ex.BzYaSuo;
import icbm.zhapin.muoxing.daodan.MMYaSuo;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExYaSuo extends DaoDan
{
	public ExYaSuo(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.setYinXin(1);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(3), new Object[] { "@?@", '@', Block.tnt, '?', Item.redstone }), this.getUnlocalizedName(), ZhuYaoICBM.CONFIGURATION, true);
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzYaSuo(world, entity, x, y, z, 2.5f).explode();
	}

	@SideOnly(Side.CLIENT)
	public MICBM getMuoXing()
	{
		return new MMYaSuo();
	}
}
