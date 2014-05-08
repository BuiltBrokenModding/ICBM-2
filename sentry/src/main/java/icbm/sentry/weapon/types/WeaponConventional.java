package icbm.sentry.weapon.types;

import icbm.Reference;
import icbm.sentry.turret.Turret;
import icbm.sentry.weapon.WeaponInaccuracy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
        this.soundEffect = Reference.PREFIX + "machinegun";
    }

    public WeaponConventional(Turret sentry, float damage)
    {
        this(sentry, 1, damage);
    }
    
    public WeaponConventional(Entity player, int ammoAmount, float damage)
    {
        super(player, ammoAmount, damage);
        this.soundEffect = Reference.PREFIX + "machinegun";
    }

    public WeaponConventional(Entity player, float damage)
    {
        this(player, 1, damage);
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return super.isAmmo(stack) && ((IAmmunition) stack.getItem()).getType(stack) == ProjectileType.CONVENTIONAL;
    }
}
