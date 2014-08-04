package icbm.explosion.explosive.blast;

import icbm.ModelICBM;
import icbm.explosion.model.tiles.MDiLei;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlastMine extends Blast
{
    public BlastMine(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doPreExplode()
    {
        if (!this.world().isRemote)
        {
            this.world().createExplosion(this.controller, position.x, position.y, position.z, 1.5f, true);
        }

        this.controller.motionX = -0.125 + 0.25 * this.world().rand.nextFloat();
        this.controller.motionY = 0.7 + 0.4 * this.world().rand.nextFloat();
        this.controller.motionZ = -0.125 + 0.25 * this.world().rand.nextFloat();
    }

    @Override
    public void doExplode()
    {
        this.controller.motionY -= 0.045;
        this.controller.rotationPitch += 1.5 * this.world().rand.nextFloat();

        if (!this.world().isRemote)
        {
            if (this.callCount < 20 * 2 && !this.controller.isCollided)
            {
                return;
            }

            if (this.callCount >= 20 * 2 && this.callCount % 2 == 0)
            {
                new BlastShrapnel(this.world(), this.exploder, this.position.x, this.position.y, this.position.z, this.getRadius(), true, true, false).doExplode();
            }

            if (this.callCount >= 20 * 2 + 20)
            {
                this.controller.endExplosion();
            }
        }
    }

    @Override
    public boolean isMovable()
    {
        return true;
    }

    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public long getEnergy()
    {
        return 8000;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getRenderModel()
    {
        return MDiLei.INSTANCE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getRenderResource()
    {
        return MDiLei.TEXTURE;
    }

}
