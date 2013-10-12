package icbm.explosion.model.missiles;

import icbm.api.IMissile;
import icbm.core.base.ModelICBM;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MissileModule extends MissileBase
{
    public MissileModule(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.hasBlock = false;
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (entity instanceof IMissile)
        {
            ((IMissile) entity).dropMissileAsItem();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return MMYaSuo.INSTANCE;
    }
}
