package icbm.sentry.damage;

import icbm.sentry.turret.TileTurret;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class TileDamageSource extends EntityDamageSource
{
	public TileDamageSource(String damageName, TileTurret tileEntity)
	{
		super(damageName, tileEntity.getDamageEntity());
	}

	public static TileDamageSource doBulletDamage(TileTurret tileEntity)
	{
		return (TileDamageSource) (new TileDamageSource("bullet", tileEntity).setProjectile());
	}

	public static TileDamageSource doLaserDamage(TileTurret tileEntity)
	{
		return (TileDamageSource) (new TileDamageSource("laser", tileEntity).setDamageBypassesArmor().setProjectile());
	}

	@Override
	public DamageSource setDamageBypassesArmor()
	{
		return super.setDamageBypassesArmor();
	}
}
