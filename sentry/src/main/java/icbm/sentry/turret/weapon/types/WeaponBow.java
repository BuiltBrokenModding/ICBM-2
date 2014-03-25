package icbm.sentry.turret.weapon.types;

import icbm.sentry.turret.ai.EulerServo;
import icbm.sentry.turret.weapon.WeaponThrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.IVectorWorld;
import universalelectricity.api.vector.Vector3;

/** Weapon system that functions just like a bow
 * 
 * @author DarkGuardsman */
public class WeaponBow extends WeaponThrowable
{
    private EulerServo servo = new EulerServo(5);
    
    public WeaponBow(IVectorWorld loc)
    {
        super(loc);
        this.itemsConsumedPerShot = 1;
        this.soundEffect = "random.bow";
    }

    public WeaponBow(Entity entity)
    {
        super(entity);
        this.itemsConsumedPerShot = 1;
        this.soundEffect = "random.bow";
    }

    public WeaponBow(TileEntity tile)
    {
        super(tile);
        this.itemsConsumedPerShot = 1;
        this.soundEffect = "random.bow";
    }

    @Override
    public void fire(IVector3 entity)
    {
        EntityArrow entityarrow = new EntityArrow(world());
        Vector3 end = this.getBarrelEnd();
        this.servo.set(0, yaw());
        this.servo.set(1, pitch());
        Vector3 vel = servo.toVector().scale(3);

        entityarrow.setLocationAndAngles(end.x(), end.y(), end.z(), (float) yaw() + 90, (float) pitch());

        entityarrow.yOffset = 0.0F;

        entityarrow.motionX = vel.x;
        entityarrow.motionZ = vel.z;
        entityarrow.motionY = vel.y;

        world().spawnEntityInWorld(entityarrow);
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
        EntityArrow arrow = new EntityArrow(world, x, y, z);
        arrow.canBePickedUp = 1;
        return arrow;
    }

    @Override
    protected float getSpread()
    {
        return 2.0F;
    }

    @Override
    protected float getVelocity()
    {
        return 2F;
    }
}
