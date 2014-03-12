package icbm.sentry.turret.weapon;

import icbm.api.sentry.IAmmunition;
import icbm.api.sentry.ProjectileType;
import icbm.explosion.explosive.EntityExplosion;
import icbm.sentry.turret.Turret;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.vector.Cuboid;

/** High powered electro magnetic cannon designed to throw a small metal object up to sonic speeds
 * 
 * @author Darkguardsman */
public class WeaponRailgun extends WeaponProjectile implements IEnergyWeapon
{
    private long energyCost = 1000000;

    public WeaponRailgun(Turret sentry)
    {
        this(sentry, 100);
    }

    public WeaponRailgun(Turret sentry, float damage)
    {
        super(sentry, 1, damage);
    }

    public WeaponRailgun(Turret sentry, float damage, long energy)
    {
        this(sentry, damage);
        this.energyCost = energy;
    }

    @Override
    public void onHitEntity(Entity entity)
    {
        super.onHitEntity(entity);
        onHitBlock(Vector3.fromCenter(entity));
    }

    @SuppressWarnings("unused")
    @Override
    public void onHitBlock(Vector3 hit)
    {
        int size = 10;

        /** Kill all active explosives with antimatter. */
        if (false)
        {
            AxisAlignedBB bounds = new Cuboid().expand(50).translate(hit).toAABB();
            List<EntityExplosion> entities = this.turret.world().getEntitiesWithinAABB(EntityExplosion.class, bounds);

            for (EntityExplosion entity : entities)
            {
                entity.endExplosion();
            }
            size = 20;
        }

        // TODO: Fix this null.
        this.turret.world().newExplosion((Entity) null, hit.x, hit.y, hit.z, size, true, true);

        Block block = Block.blocksList[this.turret.world().getBlockId(hit.intX(), hit.intY(), hit.intZ())];
        if (block != null && block.getBlockHardness(this.turret.world(), hit.intX(), hit.intY(), hit.intZ()) >= 0)
        {
            this.turret.world().setBlockToAir(hit.intX(), hit.intY(), hit.intZ());
        }
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return super.isAmmo(stack) && ((IAmmunition) stack.getItem()).getType(stack) == ProjectileType.RAILGUN;
    }

    @Override
    public void fire(Vector3 target)
    {
        double d = target.distance(this.turret.getAbsoluteCenter());
        Vector3 normalized = target.clone().subtract(turret.getAbsoluteCenter()).normalize();
        target.translate(normalized.scale(8));

        for (int i = 0; i < 5; i++)
        {
            doFire(target.clone().translate(getInaccuracy(d), getInaccuracy(d), getInaccuracy(d)));
        }

        consumeAmmo(ammoAmount, true);
    }

    @Override
    public long getEnergyPerShot()
    {
        return this.energyCost;
    }
}
