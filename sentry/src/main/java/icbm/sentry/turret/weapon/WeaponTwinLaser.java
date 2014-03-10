package icbm.sentry.turret.weapon;

import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.Turret;
import universalelectricity.api.vector.Vector3;

/**
 * Double barreled version of the laser
 * 
 * @author Darkguardsman
 */
public class WeaponTwinLaser extends WeaponLaser
{
	public WeaponTwinLaser(Turret sentry, float damage)
	{
		super(sentry, damage);
	}

	public void fireClient(Vector3 hit)
	{
		Vector3 center = new Vector3(turret.getHost().x(), turret.getHost().y(), turret.getHost().z()).add(turret.getCenterOffset());
		ICBMSentry.proxy.renderBeam(turret.getHost().world(), Vector3.translate(center, new Vector3(turret.getServo().yaw - 6, turret.getServo().pitch * 1.4f).scale(1.2)), hit, 1, 0.4f, 0.4f, 10);
		ICBMSentry.proxy.renderBeam(turret.getHost().world(), Vector3.translate(center, new Vector3(turret.getServo().yaw + 6, turret.getServo().pitch * 1.4f).scale(1.2)), hit, 1, 0.4f, 0.4f, 10);
	}
}
