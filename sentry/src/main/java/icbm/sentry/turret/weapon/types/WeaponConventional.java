package icbm.sentry.turret.weapon.types;

import icbm.sentry.turret.Turret;
import icbm.sentry.turret.weapon.WeaponInaccuracy;
import net.minecraft.item.ItemStack;
import calclavia.api.icbm.sentry.IAmmunition;
import calclavia.api.icbm.sentry.ProjectileType;

/** Conventional bullet driven weapon system that are commonly used and known.
 * 
 * @author DarkGuardsman */
public class WeaponConventional extends WeaponInaccuracy
{
    public WeaponConventional(Turret sentry, int ammoAmount, float damage)
    {
        super(sentry, ammoAmount, damage);
    }

    public WeaponConventional(Turret sentry, float damage)
    {
        this(sentry, 1, damage);
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return super.isAmmo(stack) && ((IAmmunition) stack.getItem()).getType(stack) == ProjectileType.CONVENTIONAL;
    }
}
