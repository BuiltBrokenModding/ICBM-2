package icbm.sentry.turret.weapon;

import icbm.Reference;
import icbm.sentry.ICBMSentry;
import icbm.sentry.interfaces.IEnergyWeapon;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** @author DarkGuardsman */
public class WeaponLaser extends WeaponDamage implements IEnergyWeapon
{
    private long energyCost = 10000;

    public WeaponLaser(Turret sentry, float damage, long energy)
    {
        this(sentry, damage);
        this.energyCost = energy;
    }

    public WeaponLaser(Turret sentry, float damage)
    {
        super(sentry, TurretDamageSource.turretLaser, damage);
    }

    @Override
    public void fire(Entity entity)
    {
        this.onHitEntity(entity);
        world().playSoundEffect(x(), y(), z(), Reference.PREFIX + "lasershot", 5F, 1F - (world().rand.nextFloat() * 0.2f));
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
        /** placeholder code to not accidentally call a traveling bullet style fire. --tgame14 */
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void fireClient(Vector3 hit)
    {
        ICBMSentry.proxy.renderBeam(world(), getBarrelEnd(), hit, 1F, 1F, 1F, 10);
    }

    @Override
    public long getEnergyPerShot()
    {
        return this.energyCost;
    }
}
