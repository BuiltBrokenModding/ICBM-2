package icbm.sentry.turret.cart;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

public class EntityTurretCart extends EntityMinecart
{

    public EntityTurretCart(World world)
    {
        super(world);
    }
    public EntityTurretCart(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    @Override
    public int getMinecartType()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
