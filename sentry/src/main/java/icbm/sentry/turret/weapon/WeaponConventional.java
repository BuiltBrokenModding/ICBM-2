package icbm.sentry.turret.weapon;

import icbm.sentry.turret.Turret;
import net.minecraft.item.ItemStack;
import calclavia.api.icbm.sentry.IAmmunition;
import calclavia.api.icbm.sentry.ProjectileType;

/** Conventional bullet driven weapon system that are commonly used and known.
 * 
 * @author DarkGuardsman */
public class WeaponConventional extends WeaponGun
{
    public WeaponConventional(Turret sentry, int ammoAmount, float damage)
    {
        super(sentry, ammoAmount, damage);
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return super.isAmmo(stack) && ((IAmmunition) stack.getItem()).getType(stack) == ProjectileType.CONVENTIONAL;
    }
}
