package icbm.gangshao.damage;

import icbm.gangshao.turret.TPaoTaiBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class TileDamageSource extends EntityDamageSource
{
	public TileDamageSource(String damageName, TPaoTaiBase tileEntity)
	{
		super(damageName, tileEntity.getDamageEntity());
	}

	public static TileDamageSource doBulletDamage(TPaoTaiBase tileEntity)
	{
		return (TileDamageSource) (new TileDamageSource("bullet", tileEntity).setProjectile());
	}

	public static TileDamageSource doLaserDamage(TPaoTaiBase tileEntity)
	{
		return (TileDamageSource) (new TileDamageSource("laser", tileEntity).setDamageBypassesArmor().setProjectile());
	}

	@Override
	public DamageSource setDamageBypassesArmor()
	{
		return super.setDamageBypassesArmor();
	}
}
