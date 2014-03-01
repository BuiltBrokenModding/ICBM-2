package icbm.sentry.turret.weapon;

import icbm.Reference;
import icbm.sentry.ICBMSentry;
import icbm.sentry.interfaces.ISentry;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Double barreled version of the laser
 * 
 * @author Darkguardsman
 */
public class WeaponTwinLaser extends WeaponLaser
{
	public WeaponTwinLaser(ISentry sentry, float damage)
	{
		super(sentry, damage);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderClient(Vector3 hit)
	{
		Vector3 center = new Vector3(sentry.getHost().x(), sentry.getHost().y(), sentry.getHost().z()).add(sentry.getCenterOffset());
		ICBMSentry.proxy.renderBeam(sentry.getHost().world(), Vector3.translate(center, Vector3.getDeltaPositionFromRotation(this.sentry.getHost().yaw() - 6, this.sentry.getHost().pitch() * 1.4f).scale(1.2)), hit, 1, 0.4f, 0.4f, 5);
		ICBMSentry.proxy.renderBeam(sentry.getHost().world(), Vector3.translate(center, Vector3.getDeltaPositionFromRotation(this.sentry.getHost().yaw() + 6, this.sentry.getHost().pitch() * 1.4f).scale(1.2)), hit, 1, 0.4f, 0.4f, 5);
		this.sentry.getHost().world().playSoundEffect(sentry.getHost().x(), sentry.getHost().y(), sentry.getHost().z(), Reference.PREFIX + "lasershot", 5F, 1F - (sentry.getHost().world().rand.nextFloat() * 0.2f));
	}
}
