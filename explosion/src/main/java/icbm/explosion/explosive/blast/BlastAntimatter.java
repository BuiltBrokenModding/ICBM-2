package icbm.explosion.explosive.blast;

import icbm.Reference;
import icbm.explosion.explosive.EntityExplosion;
import icbm.explosion.explosive.Blast;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

public class BlastAntimatter extends Blast
{
    private boolean destroyBedrock;

    public BlastAntimatter(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastAntimatter(World world, Entity entity, double x, double y, double z, float size, boolean destroyBedrock)
    {
        this(world, entity, x, y, z, size);
        this.destroyBedrock = destroyBedrock;
    }

    /** Called before an explosion happens */
    @Override
    public void doPreExplode()
    {
        super.doPreExplode();
        this.worldObj.playSoundEffect(this.position.x, this.position.y, this.position.z, Reference.PREFIX + "antimatter", 7F, (float) (this.worldObj.rand.nextFloat() * 0.1 + 0.9F));
        this.doDamageEntities(this.getRadius() * 2, Integer.MAX_VALUE);
    }

    @Override
    public void doExplode()
    {
        if (!this.worldObj.isRemote)
        {
            for (int x = (int) -this.getRadius(); x < this.getRadius(); x++)
            {
                for (int y = (int) -this.getRadius(); y < this.getRadius(); y++)
                {
                    for (int z = (int) -this.getRadius(); z < this.getRadius(); z++)
                    {
                        Vector3 targetPosition = this.position.clone().translate(new Vector3(x, y, z));

                        double dist = position.distance(targetPosition);

                        if (dist < this.getRadius())
                        {
                            int blockID = targetPosition.getBlockID(worldObj);
                            Block block = Block.blocksList[blockID];

                            if (block != null && !block.isAirBlock(this.worldObj, x, y, x))
                            {
                                if (!this.destroyBedrock && block.getBlockHardness(this.worldObj, x, y, x) < 0)
                                {
                                    continue;
                                }

                                if (dist < this.getRadius() - 1 || worldObj.rand.nextFloat() > 0.7)
                                {
                                    targetPosition.setBlock(worldObj, 0);
                                }
                            }
                        }
                    }

                }
            }
        }

        // TODO: Render antimatter shockwave
        /*
         * else if (ZhuYao.proxy.isGaoQing()) { for (int x = -this.getRadius(); x <
         * this.getRadius(); x++) { for (int y = -this.getRadius(); y < this.getRadius(); y++) { for
         * (int z = -this.getRadius(); z < this.getRadius(); z++) { Vector3 targetPosition =
         * Vector3.add(position, new Vector3(x, y, z)); double distance =
         * position.distanceTo(targetPosition);
         * if (targetPosition.getBlockID(worldObj) == 0) { if (distance < this.getRadius() &&
         * distance > this.getRadius() - 1 && worldObj.rand.nextFloat() > 0.5) {
         * ParticleSpawner.spawnParticle("antimatter", worldObj, targetPosition); } } } } } }
         */
    }

    @Override
    public void doPostExplode()
    {
        this.doDamageEntities(this.getRadius() * 2, Integer.MAX_VALUE);
    }

    @Override
    protected boolean onDamageEntity(Entity entity)
    {
        if (entity instanceof EntityExplosion)
        {
            if (((EntityExplosion) entity).baoZha instanceof BlastRedmatter)
            {
                entity.setDead();
                return true;
            }
        }

        return false;
    }

    @Override
    public float getEnergy()
    {
        return 30000000;
    }
}
