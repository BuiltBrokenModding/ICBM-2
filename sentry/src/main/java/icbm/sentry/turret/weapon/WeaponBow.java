package icbm.sentry.turret.weapon;

import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.Vector3;
import icbm.sentry.turret.Turret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** Weapon system that functions just like a bow
 * 
 * @author DarkGuardsman */
public class WeaponBow extends WeaponGun
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
    public void fire(IVector3 target)
    {
        consumeAmmo(itemsConsumedPerShot, true);
        Vector3 end = this.getBarrelEnd();
        EntityArrow arrow = new EntityArrow(world(), end.x, end.y, end.z);
        arrow.setThrowableHeading(target.x(), target.y(), target.z(), 1.1f, 6f);
        world().spawnEntityInWorld(arrow);
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return stack != null && stack.getItem() == Item.arrow;
    }

    @Override
    public void fireClient(IVector3 hit)
    {

    }

}
