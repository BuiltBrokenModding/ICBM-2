package icbm.sentry.turret.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.IVectorWorld;
import universalelectricity.api.vector.Vector3;

/** Prefab weapon system for object that are cast or throw forward. Examples of this are eggs,
 * arrows, or grenades.
 * 
 * @author Darkguardsman */
public abstract class WeaponThrowable extends WeaponSystem
{
    public WeaponThrowable(IVectorWorld loc)
    {
        super(loc);
    }

    public WeaponThrowable(Entity entity)
    {
        super(entity);
    }

    public WeaponThrowable(TileEntity tile)
    {
        super(tile);
    }

    @Override
    protected void playFiringAudio()
    {
        world().playAuxSFX(1002, (int) x(), (int) y(), (int) z(), 0);
    }

    @Override
    protected void doFire(IVector3 target)
    {
        Vector3 end = this.getBarrelEnd();
        IProjectile projectile = this.getProjectileEntity(world(), end.x(), end.y(), end.z());
        if (projectile instanceof Entity)
        {
            projectile.setThrowableHeading(target.x(), target.y(), target.z(), this.getVelocity(), this.getSpread());
            world().spawnEntityInWorld((Entity) projectile);
        }
    }

    @Override
    protected void onHitEntity(Entity entity)
    {

    }

    @Override
    protected void onHitBlock(Vector3 block)
    {

    }

    protected abstract IProjectile getProjectileEntity(World world, double x, double y, double z);

    protected float getSpread()
    {
        return 6.0F;
    }

    protected float getVelocity()
    {
        return 1.1F;
    }

}
