package icbm.sentry.turret.auto;

import icbm.Reference;
import icbm.sentry.turret.ai.TurretAntiAirSelector;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.WeaponConventional;
import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;

/** AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman */
public class TurretAntiAir extends TurretAuto
{
    public TurretAntiAir(TileTurret host)
    {
        super(host);
        this.weaponSystem = new WeaponConventional(this, 1, 10)
        {
            @Override
            public void fire(Vector3 target)
            {
                super.fire(target);
                turret.getHost().world().playSoundEffect(turret.getHost().x(), turret.getHost().y(), turret.getHost().z(), Reference.PREFIX + "aagun", 5F, 1F - (turret.getHost().world().rand.nextFloat() * 0.2f));
            }
        };
        this.centerOffset.y = 0.75;
        this.default_target_range = 200;
        this.maxCooldown = 5;
        selector = new TurretAntiAirSelector(this);
    }

    @Override
    public void setTarget(Entity target)
    {
        super.setTarget(target);
        if (this.target != null)
        {
            this.getServo().setRotationSpeed(20);
        }
        else
        {
            this.getServo().setRotationSpeed(5);
        }
    }
}
