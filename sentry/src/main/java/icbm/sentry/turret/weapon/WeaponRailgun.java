package icbm.sentry.turret.weapon;

import java.util.List;

import calclavia.lib.prefab.vector.Cuboid;
import net.minecraft.block.Block;
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

    public void fire(boolean isAntimatter)
    {
        this.turret.world().playSoundEffect(this.turret.x(), this.turret.y(), this.turret.z(), Reference.PREFIX + "railgun", 5F, 0.9f + this.turret.world().rand.nextFloat() * 0.2f);
        
        int explosionDepth = 10;
        Vector3 hit = null;
        //TODO: Change this to create a mini explosion instead of creating its own
        while (explosionDepth > 0)
        {
            MovingObjectPosition objectMouseOver = this.turret.getAi().rayTrace(2000);

            if (objectMouseOver != null)
            {
                hit = new Vector3(objectMouseOver.hitVec);

                /** Kill all active explosives with antimatter. */
                if (isAntimatter)
                {
                    int radius = 50;
                    AxisAlignedBB bounds = new Cuboid().expand(radius).translate(hit).toAABB();
                    List<EntityExplosion> entities = this.turret.world().getEntitiesWithinAABB(EntityExplosion.class, bounds);

                    for (EntityExplosion entity : entities)
                    {
                        entity.endExplosion();
                    }
                }
                Block block = Block.blocksList[hit.getBlockID(this.turret.world())];
                if (block != null && block.blockResistance >= 0)
                {
                    hit.setBlock(this.turret.world(), 0);
                }

                // TODO: Fix this null.
                this.turret.world().newExplosion(null, hit.x, hit.y, hit.z, 10, true, true);
            }

            explosionDepth--;
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
        consumeAmmo(ammoAmount, true);
    }
}
