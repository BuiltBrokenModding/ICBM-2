package icbm.sentry.turret.weapon.types;

import icbm.sentry.turret.weapon.WeaponThrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.api.vector.IVectorWorld;

/** Weapon system that functions just like a bow
 * 
 * @author DarkGuardsman */
public class WeaponBow extends WeaponThrowable
{
    public WeaponBow(IVectorWorld loc)
    {
        super(loc);
        this.itemsConsumedPerShot = 1;
    }

    public WeaponBow(Entity entity)
    {
        super(entity);
        this.itemsConsumedPerShot = 1;
    }

    public WeaponBow(TileEntity tile)
    {
        super(tile);
        this.itemsConsumedPerShot = 1;
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        //TODO add support for other mod's arrows
        return stack != null && stack.getItem() == Item.arrow;
    }

    @Override
    protected IProjectile getProjectileEntity(World world, double x, double y, double z)
    {
        return new EntityArrow(world, x, y, z);
    }
}
