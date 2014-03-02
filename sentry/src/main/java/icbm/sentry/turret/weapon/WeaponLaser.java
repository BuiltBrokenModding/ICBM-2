package icbm.sentry.turret.weapon;

import icbm.Reference;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** @author DarkGuardsman */
public class WeaponLaser extends WeaponProjectile
{
	public WeaponLaser(Turret sentry, float damage)
	{
		super(sentry, damage);
	}

	@Override
	public void fire(Entity entity)
	{
		this.onHitEntity(entity);
	}

	@Override
	public void onHitEntity(Entity entity)
	{
		if (entity != null)
		{
			super.onHitEntity(entity);
			entity.setFire(5);
		}
	}

	@Override
	public void fire(Vector3 target)
	{
		turret.getHost().world().playSoundEffect(turret.getHost().x(), turret.getHost().y(), turret.getHost().z(), Reference.PREFIX + "lasershot", 5F, 1F - (turret.getHost().world().rand.nextFloat() * 0.2f));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void fireClient(Vector3 hit)
	{
		ICBMSentry.proxy.renderBeam(turret.getHost().world(), getBarrelEnd(), hit, 1F, 1F, 1F, 10);
	}
}
