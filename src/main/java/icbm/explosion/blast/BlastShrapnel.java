package icbm.explosion.blast;

import icbm.content.entity.EntityFragments;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlastShrapnel extends Blast
{
    private boolean isExplosive = false;
    private boolean isAnvil = false;

    public BlastShrapnel(World world, Entity entity, double x, double y, double z, float size, boolean isFlaming, boolean isExplosive, boolean isAnvil)
    {
        this(world, entity, x, y, z, size);
        this.isFlaming = isFlaming;
        this.isExplosive = isExplosive;
        this.isAnvil = isAnvil;
    }

    public BlastShrapnel(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doExplode()
    {
        if (!world().isRemote)
        {
            float amountToRotate = 360 / this.getRadius();

            for (int i = 0; i < this.getRadius(); i++)
            {
                // Try to do a 360 explosion on all 6 faces of the cube.
                float rotationYaw = 0.0F + amountToRotate * i;

                for (int ii = 0; ii < this.getRadius(); ii++)
                {
                    EntityFragments arrow = new EntityFragments(world(), position.x(), position.y(), position.z(), this.isExplosive, this.isAnvil);

                    if (this.isFlaming)
                    {
                        arrow.arrowCritical = true;
                        arrow.setFire(100);
                    }

                    float rotationPitch = 0.0F + amountToRotate * ii;
                    arrow.setLocationAndAngles(position.x(), position.y(), position.z(), rotationYaw, rotationPitch);
                    arrow.posX -= (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
                    arrow.posY -= 0.10000000149011612D;
                    arrow.posZ -= (MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
                    arrow.setPosition(arrow.posX, arrow.posY, arrow.posZ);
                    arrow.yOffset = 0.0F;
                    arrow.motionX = (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
                    arrow.motionZ = (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
                    arrow.motionY = (-MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI));
                    arrow.setArrowHeading(arrow.motionX * world().rand.nextFloat(), arrow.motionY * world().rand.nextFloat(), arrow.motionZ * world().rand.nextFloat(), 0.5f + (0.7f * world().rand.nextFloat()), 1.0F);
                    world().spawnEntityInWorld(arrow);

                }
            }
        }
    }

    @Override
    public long getEnergy()
    {
        return 1000;
    }
}
