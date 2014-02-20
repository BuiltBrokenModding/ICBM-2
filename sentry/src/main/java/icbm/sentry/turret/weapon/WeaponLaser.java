package icbm.sentry.turret.weapon;

import icbm.sentry.ICBMSentry;
import icbm.sentry.interfaces.ISentry;
import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** @author DarkGuardsman */
public class WeaponLaser extends WeaponSystemProjectile
{
    public WeaponLaser(ISentry sentry, float damage)
    {
        super(sentry, damage);
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
    public void onHitBlock(Vector3 block)
    {
        //TODO if the laser hits the same block for 5 sec destroy
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderClient(Vector3 hit)
    {
        ICBMSentry.proxy.renderBeam(sentry.getHost().world(), getBarrelEnd(), hit, 1F, 1F, 1F, 10);
    }
}
