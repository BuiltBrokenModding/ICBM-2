package icbm.sentry.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import universalelectricity.api.vector.IVectorWorld;
import universalelectricity.api.vector.Vector3;

/** Any weapon that deals damage.
 * 
 * @author Calclavia */
public class WeaponDamage extends WeaponRaytrace
{
    protected float damage = 5f;
    protected DamageSource damageSource;

    public WeaponDamage(IVectorWorld sentry, DamageSource damageSource, float damage)
    {
        super(sentry);
        this.damage = damage;
        this.damageSource = damageSource;
    }

    public WeaponDamage(Entity entity, DamageSource damageSource, float damage)
    {
        super(entity);
        this.damage = damage;
        this.damageSource = damageSource;
    }

    public WeaponDamage(TileEntity tile, DamageSource damageSource, float damage)
    {
        super(tile);
        this.damage = damage;
        this.damageSource = damageSource;
    }

    @Override
    public void onHitEntity(Entity entity)
    {
        if (entity != null)
        {
        	System.out.println("Entity: " + entity + " suffered " + damage + " damage!");
            entity.attackEntityFrom(damageSource, damage);
        }
    }

    @Override
    protected void onHitBlock(Vector3 block)
    {
        // TODO Add a way for basic rounds to bounce off or terrain based on material type
    }
}
