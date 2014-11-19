package icbm.explosion.blast;

import icbm.Reference;
import icbm.ICBM;

import java.util.List;

import icbm.explosion.Blast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import resonant.lib.transform.vector.Vector3;

public class BlastEnderman extends Blast
{
    public int duration = 20 * 8;
    private Vector3 teleportTarget;

    public BlastEnderman(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastEnderman(World world, Entity entity, double x, double y, double z, float size, Vector3 teleportTarget)
    {
        super(world, entity, x, y, z, size);
        this.teleportTarget = teleportTarget;
    }

    @Override
    public void doExplode()
    {
        if (this.world().isRemote)
        {
            int r = (int) (this.getRadius() - ((double) this.callCount / (double) this.duration) * this.getRadius());

            for (int x = -r; x < r; x++)
            {
                for (int z = -r; z < r; z++)
                {
                    for (int y = -r; y < r; y++)
                    {
                        Vector3 targetPosition = position.add(new Vector3(x, y, z));

                        double distance = targetPosition.distance(position);

                        if (distance < r && distance > r - 1)
                        {
                            if (!targetPosition.isAirBlock(world()))
                                continue;

                            if (this.world().rand.nextFloat() < Math.max(0.001 * r, 0.01))
                            {
                                float velX = (float) ((targetPosition.x() - position.x()) * 0.6);
                                float velY = (float) ((targetPosition.y() - position.y()) * 0.6);
                                float velZ = (float) ((targetPosition.z() - position.z()) * 0.6);

                                ICBM.proxy.spawnParticle("portal", world(), targetPosition, velX, velY, velZ, 5f, 1);
                            }
                        }
                    }
                }
            }
        }

        int radius = (int) this.getRadius();
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x() - radius, position.y() - radius, position.z() - radius, position.x() + radius, position.y() + radius, position.z() + radius);
        List<Entity> allEntities = world().getEntitiesWithinAABB(Entity.class, bounds);
        boolean explosionCreated = false;

        for (Entity entity : allEntities)
        {
            if (entity != this.controller)
            {

                double xDifference = entity.posX - position.x();
                double yDifference = entity.posY - position.y();
                double zDifference = entity.posZ - position.z();

                int r = (int) this.getRadius();
                if (xDifference < 0)
                    r = (int) -this.getRadius();

                entity.motionX -= (r - xDifference) * Math.abs(xDifference) * 0.0006;

                r = (int) this.getRadius();
                if (entity.posY > position.y())
                    r = (int) -this.getRadius();
                entity.motionY += (r - yDifference) * Math.abs(yDifference) * 0.0011;

                r = (int) this.getRadius();
                if (zDifference < 0)
                    r = (int) -this.getRadius();

                entity.motionZ -= (r - zDifference) * Math.abs(zDifference) * 0.0006;

                if (new Vector3(entity.posX, entity.posY, entity.posZ).subtract(position).magnitude() < 4)
                {
                    if (!explosionCreated && callCount % 5 == 0)
                    {
                        world().spawnParticle("hugeexplosion", entity.posX, entity.posY, entity.posZ, 0.0D, 0.0D, 0.0D);
                        explosionCreated = true;
                    }

                    try
                    {
                        /** If a target doesn't exist, search for a random one within 100 block
                         * range. */
                        if (this.teleportTarget == null)
                        {
                            int checkY;
                            int checkX = this.world().rand.nextInt(300) - 150 + (int) this.controller.posX;
                            int checkZ = this.world().rand.nextInt(300) - 150 + (int) this.controller.posZ;

                            for (checkY = 63; !this.world().isAirBlock(checkX, checkY, checkZ) && !this.world().isAirBlock(checkX, checkY + 1, checkZ); ++checkY)
                            {
                                ;
                            }

                            this.teleportTarget = new Vector3(checkX, checkY, checkZ);
                        }

                        this.world().playSoundAtEntity(entity, "mob.endermen.portal", 1.0F, 1.0F);

                        if (entity instanceof EntityPlayerMP)
                        {
                            ((EntityPlayerMP) entity).playerNetServerHandler.setPlayerLocation(this.teleportTarget.x() + 0.5, this.teleportTarget.y() + 0.5, this.teleportTarget.z() + 0.5, entity.rotationYaw, entity.rotationPitch);
                        }
                        else
                        {
                            entity.setPosition(this.teleportTarget.x() + 0.5, this.teleportTarget.y() + 0.5, this.teleportTarget.z() + 0.5);
                        }

                    }
                    catch (Exception e)
                    {
                        Reference.LOGGER.severe("Failed to teleport entity to the End.");
                        e.printStackTrace();
                    }
                }
            }
        }

        this.world().playSound(this.position.x(), this.position.y(), this.position.z(), "portal.portal", 2F, world().rand.nextFloat() * 0.4F + 0.8F, false);

        if (this.callCount > this.duration)
        {
            this.controller.endExplosion();
        }
    }

    @Override
    public void doPostExplode()
    {
        super.doPostExplode();

        if (!this.world().isRemote)
        {
            for (int i = 0; i < 8; i++)
            {
                EntityEnderman enderman = new EntityEnderman(world());
                enderman.setPosition(this.position.x(), this.position.y(), this.position.z());
                this.world().spawnEntityInWorld(enderman);
            }
        }
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @return - Return -1 if this explosive does not need proceudral calls */
    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public float getRadius()
    {
        return 20;
    }

    @Override
    public long getEnergy()
    {
        return 0;
    }

    @Override
    public boolean isMovable()
    {
        return true;
    }
}
