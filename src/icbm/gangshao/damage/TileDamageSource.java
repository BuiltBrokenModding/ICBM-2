package icbm.gangshao.damage;

import icbm.gangshao.turret.TPaoDaiBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class TileDamageSource extends EntityDamageSource
{
	public TileDamageSource(String damageName, TPaoDaiBase tileEntity)
	{
		super(damageName, tileEntity.getDamageEntity());
	}

	public static TileDamageSource doBulletDamage(TPaoDaiBase tileEntity)
	{
		return (TileDamageSource) (new TileDamageSource("bullet", tileEntity).setProjectile());
	}

	public static TileDamageSource doLaserDamage(TPaoDaiBase tileEntity)
	{
		return (TileDamageSource) (new TileDamageSource("laser", tileEntity).setDamageBypassesArmor().setProjectile());
	}

	public DamageSource setDamageBypassesArmor()
	{
		return super.setDamageBypassesArmor();
	}
}
