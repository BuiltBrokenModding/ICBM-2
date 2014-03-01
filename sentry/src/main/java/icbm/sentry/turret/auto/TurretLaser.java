package icbm.sentry.turret.auto;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponTwinLaser;
import net.minecraft.util.MathHelper;

public class TurretLaser extends AutoSentry
{
    /** Laser turret spins its barrels every shot. */
    public float barrelRotation;
    public float barrelRotationVelocity;

    public TurretLaser(TileTurret host)
    {
        super(host);
        maxHealth = 50;
        weaponSystem = new WeaponTwinLaser(this, 15);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        
        if (this.world().isRemote)
        {
            this.barrelRotation = MathHelper.wrapAngleTo180_float(this.barrelRotation + this.barrelRotationVelocity);
            this.barrelRotationVelocity = Math.max(this.barrelRotationVelocity - 0.1f, 0);
        }
    }
}
