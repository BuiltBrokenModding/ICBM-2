package icbm.zhapin.zhapin.ex;

import icbm.core.ZhuYaoICBM;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzFanWuSu;
import icbm.zhapin.muoxing.daodan.MMFanWuSu;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExFanWuSu extends DaoDan
{
	public boolean destroyBedrock = true;

	public ExFanWuSu(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.setYinXin(300);
		this.destroyBedrock = ZhuYaoICBM.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Antimatter Destroy Bedrock", this.destroyBedrock).getBoolean(this.destroyBedrock);
	}

	/**
	 * Called when the explosive is on fuse and going to explode. Called only when the explosive is
	 * in it's TNT form.
	 * 
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
		super.onYinZha(worldObj, position, fuseTicks);

		if (fuseTicks % 25 == 0)
		{
			worldObj.playSoundEffect(position.x, position.y, position.z, ZhuYaoICBM.PREFIX + "alarm", 4F, 1F);
		}
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', ZhaPin.yuanZi.getItemStack(), 'A', "antimatterGram" }), this.getUnlocalizedName(), ZhuYaoICBM.CONFIGURATION, true);
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzFanWuSu(world, entity, x, y, z, 40, this.destroyBedrock).explode();
	}

	@Override
	public MICBM getMuoXing()
	{
		return new MMFanWuSu();
	}
}
