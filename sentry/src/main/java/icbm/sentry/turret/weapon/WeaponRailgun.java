package icbm.sentry.turret.weapon;

import net.minecraft.item.ItemStack;
import universalelectricity.api.vector.Vector3;
import icbm.api.sentry.IAmmunition;
import icbm.api.sentry.ProjectileType;
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
