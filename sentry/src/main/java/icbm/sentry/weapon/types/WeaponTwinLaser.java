package icbm.sentry.weapon.types;

import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.Turret;
import universalelectricity.api.vector.Vector3;

/** Double barreled version of the laser
 * 
 * @author Darkguardsman */
public class WeaponTwinLaser extends WeaponLaser
{
    public WeaponTwinLaser(Turret sentry, float damage, long energy)
    {
        super(sentry, damage, energy);
    }

    public void fireClient(Vector3 hit)
    {
        Vector3 center = new Vector3(x(), y(), z());
        ICBMSentry.proxy.renderBeam(world(), Vector3.translate(center, new Vector3(yaw() - 6, pitch() * 1.4f).scale(1.2)), hit, 1, 0.4f, 0.4f, 10);
        ICBMSentry.proxy.renderBeam(world(), Vector3.translate(center, new Vector3(yaw() + 6, pitch() * 1.4f).scale(1.2)), hit, 1, 0.4f, 0.4f, 10);
    }
}
