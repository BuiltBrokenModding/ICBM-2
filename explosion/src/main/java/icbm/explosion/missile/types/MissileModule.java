package icbm.explosion.missile.types;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import calclavia.api.icbm.IMissile;

public class MissileModule extends MissileBase
{
    public MissileModule()
    {
        super("missileModule", 1);
        this.hasBlock = false;
        this.modelName = "missile_conventional.tcn";
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (entity instanceof IMissile)
        {
            ((IMissile) entity).dropMissileAsItem();
        }
    }
}
