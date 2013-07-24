package icbm.zhapin.zhapin.daodan;

import icbm.api.IMissile;
import icbm.core.MICBM;
import icbm.zhapin.muoxing.daodan.MMYaSuo;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DModule extends DaoDanTeBie
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
	public MICBM getMissileModel()
	{
		return MMYaSuo.INSTANCE;
	}
}
