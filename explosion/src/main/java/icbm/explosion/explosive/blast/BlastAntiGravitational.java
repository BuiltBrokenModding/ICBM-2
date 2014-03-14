package icbm.explosion.explosive.blast;

import icbm.Reference;
import icbm.explosion.EntityFlyingBlock;
import icbm.explosion.explosive.Blast;
import icbm.explosion.explosive.thread.ThreadSmallExplosion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import calclavia.api.mffs.IForceFieldBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

public class BlastAntiGravitational extends Blast
{
    protected ThreadSmallExplosion thread;
    protected Set<EntityFlyingBlock> feiBlocks = new HashSet<EntityFlyingBlock>();

    public BlastAntiGravitational(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doPreExplode()
    {
        if (!this.worldObj.isRemote)
        {
            this.thread = new ThreadSmallExplosion(this.worldObj, this.position, (int) this.getRadius(), this.exploder);
            this.thread.start();
        }

        this.worldObj.playSoundEffect(position.x, position.y, position.z, Reference.PREFIX + "antigravity", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    @Override
    public void doExplode()
    {
        int r = this.callCount;

        if (!this.worldObj.isRemote && this.thread.isComplete)
        {
            int blocksToTake = 20;

            for (Vector3 targetPosition : this.thread.results)
            {
                double distance = Vector3.distance(targetPosition, position);

                if (distance > r || distance < r - 2 || blocksToTake <= 0)
                    continue;

                int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                if (blockID == 0 || blockID == Block.bedrock.blockID || blockID == Block.obsidian.blockID)
                    continue;

                if (Block.blocksList[blockID] instanceof IForceFieldBlock)
                    continue;

                int metadata = worldObj.getBlockMetadata(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                if (distance < r - 1 || worldObj.rand.nextInt(3) > 0)
                {
                    this.worldObj.setBlockToAir(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                    targetPosition.translate(0.5D);

                    if (worldObj.rand.nextFloat() < 0.3 * (this.getRadius() - r))
                    {
                        EntityFlyingBlock entity = new EntityFlyingBlock(worldObj, targetPosition, blockID, metadata, 0);
                        worldObj.spawnEntityInWorld(entity);
                        feiBlocks.add(entity);
                        entity.yawChange = 50 * worldObj.rand.nextFloat();
                        entity.pitchChange = 100 * worldObj.rand.nextFloat();
                        entity.motionY += Math.max(0.15 * worldObj.rand.nextFloat(), 0.1);
                    }

                    blocksToTake--;
                }
            }
        }

        int radius = (int) this.getRadius();
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, 100, position.z + radius);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

        for (Entity entity : allEntities)
        {
            if (!(entity instanceof EntityFlyingBlock) && entity.posY < 100 + position.y)
            {
                if (entity.motionY < 0.4)
                {
                    entity.motionY += 0.15;
                }
            }
        }

        if (this.callCount > 20 * 120)
        {
            this.controller.endExplosion();
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
        return 15;
    }

    @Override
    public long getEnergy()
    {
        return 10000;
    }
}
