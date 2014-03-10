package icbm.sentry.turret.weapon;

import java.util.List;

import calclavia.lib.prefab.vector.Cuboid;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.Vector3;
import icbm.Reference;
import icbm.api.sentry.IAmmunition;
import icbm.api.sentry.ProjectileType;
import icbm.explosion.explosive.EntityExplosion;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.items.ItemAmmo.AmmoType;

/** High powered electro magnetic cannon designed to throw a small metal object up to sonic speeds
 * 
 * @author Darkguardsman */
public class WeaponRailgun extends WeaponProjectile
{
    public WeaponRailgun(Turret sentry)
    {
        this(sentry, 100);
    }

    public WeaponRailgun(Turret sentry, float damage)
    {
        super(sentry, 1, damage);
    }

    @Override
    public void onHitEntity(Entity entity)
    {
        super.onHitEntity(entity);
        this.onHitBlock(Vector3.fromCenter(entity));
    }

    @SuppressWarnings("unused")
    @Override
    public void onHitBlock(Vector3 hit)
    {
        System.out.println("weapon railgun on hit block");
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
        this.turret.world().newExplosion((Entity)null, hit.x, hit.y, hit.z, size, true, true);
        
        Block block = Block.blocksList[this.turret.world().getBlockId(hit.intX(), hit.intY(), hit.intZ())];
        if(block != null && block.getBlockHardness(this.turret.world(), hit.intX(), hit.intY(), hit.intZ()) >= 0)
        {
            this.turret.world().setBlockToAir(hit.intX(), hit.intY(), hit.intZ());
        }
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof IAmmunition && ((IAmmunition) stack.getItem()).getType(stack) == ProjectileType.RAILGUN;
    }

    @Override
    public void fire(Vector3 target)
    {
        System.out.println("weapon railgun fire event");
        super.fire(target);
        this.turret.world().playSoundEffect(this.turret.x(), this.turret.y(), this.turret.z(), Reference.PREFIX + "railgun", 5F, 0.9f + this.turret.world().rand.nextFloat() * 0.2f);
    }
}
