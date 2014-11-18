package icbm.explosion.blast;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlastMutation extends Blast
{
    public BlastMutation(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doExplode()
    {

        if (!this.world().isRemote)
        {
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.xf() - this.getRadius(), position.yf() - this.getRadius(), position.zf() - this.getRadius(), position.xf() + this.getRadius(), position.yf() + this.getRadius(), position.zf() + this.getRadius());
            List<EntityLiving> entitiesNearby = world().getEntitiesWithinAABB(EntityLiving.class, bounds);

            for (EntityLiving entity : entitiesNearby)
            {
                if (entity instanceof EntityPig)
                {
                    EntityPigZombie newEntity = new EntityPigZombie(world());
                    newEntity.preventEntitySpawning = true;
                    newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
                    entity.setDead();
                }
                else if (entity instanceof EntityVillager)
                {
                    EntityZombie newEntity = new EntityZombie(world());
                    newEntity.preventEntitySpawning = true;
                    newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
                    entity.setDead();
                }
            }
        }
    }

    @Override
    public float getRadius()
    {
        return 0;
    }

    @Override
    public long getEnergy()
    {
        return 50;
    }
}
