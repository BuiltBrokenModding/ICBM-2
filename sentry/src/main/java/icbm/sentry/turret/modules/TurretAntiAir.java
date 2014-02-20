package icbm.sentry.turret.modules;

import icbm.Reference;
import icbm.sentry.ClientProxy;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.TurretDamageSource;
import icbm.sentry.turret.weapon.WeaponSystemProjectile;
import universalelectricity.api.vector.Vector3;

/** AA Turret, shoots down missiles and planes.
 *
 * @author DarkGaurdsman */
public class TurretAntiAir extends AutoSentry
{
    public TurretAntiAir (TileTurret host)
    {
        super(host);
        this.centerOffset.y = 0.75;
        this.weaponSystem = new WeaponSystemProjectile(this);
    }

    @Override
    public void fireWeaponClient (Vector3 target)
    {
        this.world().playSoundEffect(this.x(), this.host.y(), this.host.z(), Reference.PREFIX + "aagun", 5F, 1F);
        ICBMSentry.proxy.renderBeam(world(), new Vector3(world().getBlockTileEntity((int) host.x(), (int) host.y(), (int) host.z())), target, 80F, 80F, 80F, 2);
    }

    @Override
    public boolean fire (Vector3 target)
    {
        this.weaponSystem.fire(target);       
        return true;
    }
}
