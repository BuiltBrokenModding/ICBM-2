package icbm.sentry.turret.auto;

import icbm.Reference;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.turret.ai.TurretAntiAirSelector;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.types.WeaponConventional;
import net.minecraft.entity.Entity;
import universalelectricity.api.vector.IVector3;

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
            public void fire(IVector3 target)
            {
                super.fire(target);
                world().playSoundEffect(x(), y(), z(), Reference.PREFIX + "aagun", 5F, 1F - (world().rand.nextFloat() * 0.2f));
            }
        };
        this.centerOffset.y = 0.75;
        applyTrait(ITurret.SEARCH_RANGE_TRAIT, 200.0);
        applyTrait(ITurret.MAX_HEALTH_TRAIT, 70.0);
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
