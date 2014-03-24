package icbm.sentry.turret.weapon;

import universalelectricity.api.vector.Vector3;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** Weapon system that functions just like a bow
 * 
 * @author DarkGuardsman */
public class WeaponBow extends WeaponProjectile
{

    public WeaponBow(Turret sentry, int ammoAmount)
    {
        super(sentry, ammoAmount, 0);
    }

    public WeaponBow(Turret sentry)
    {
        this(sentry, 1);
    }

    @Override
    public void fire(Vector3 target)
    {
        consumeAmmo(ammoAmount, true);
        Vector3 end = this.getBarrelEnd();
        EntityArrow arrow = new EntityArrow(turret.world(), end.x, end.y, end.z);
        arrow.setThrowableHeading(target.x, target.y, target.z, 1.1f, 6f);
        turret.world().spawnEntityInWorld(arrow);
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return stack != null && stack.getItem() == Item.arrow;
    }

    @Override
    public void fireClient(Vector3 hit)
    {

    }

}
