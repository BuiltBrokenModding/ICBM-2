package icbm.explosion.zhapin.daodan;

import icbm.api.IMissile;
import icbm.core.base.ModelICBM;
import icbm.explosion.model.missiles.MMYaSuo;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DModule extends MissileTeBie
{
	public DModule(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		if (entity instanceof IMissile)
		{
			((IMissile) entity).dropMissileAsItem();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelICBM getMissileModel()
	{
		return MMYaSuo.INSTANCE;
	}
}
