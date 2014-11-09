package icbm.explosion.explosive.blast;

import icbm.Reference;
import icbm.core.entity.EntityFlyingBlock;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.entities.EntityExplosion;
import icbm.explosion.entities.EntityExplosive;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import resonant.api.explosion.IExplosiveIgnore;
import resonant.lib.config.Config;
import resonant.lib.transform.Vector3;
import calclavia.api.mffs.IForceFieldBlock;

public class BlastRedmatter extends Blast
{
    private int maxTakeBlocks = 5;
    @Config(category = "Features", key = "RedMatter Life Span in ticks")
    public static int MAX_LIFESPAN = 3600; // 3 minutes
    @Config(category = "Features", key = "RedMatter despawn")
    public static boolean DO_DESPAWN = true;

    public BlastRedmatter(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doPreExplode()
    {
        if (!this.world().isRemote)
        {
            this.world().createExplosion(this.exploder, position.x, position.y, position.z, 5.0F, true);
        }
    }

    @Override
    protected void doPostExplode()
    {
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.explosionX - this.explosionSize, this.explosionY - this.explosionSize, this.explosionZ - this.explosionSize, this.explosionX + this.explosionSize, this.explosionY + this.explosionSize, this.explosionZ + this.explosionSize);
        List<?> list = this.world().getEntitiesWithinAABB(EntityExplosion.class, bounds);

        for (Object obj : list)
        {
            if (obj instanceof EntityExplosion)
            {
                EntityExplosion explosion = (EntityExplosion) obj;

                if (explosion.blast instanceof BlastRedmatter)
                {
                    explosion.setDead();
                }
            }
        }

    }

    @Override
    public void doExplode()
    {
        if (DO_DESPAWN && callCount >= MAX_LIFESPAN)
        {
            this.postExplode();
        }

        // Try to find and grab some blocks to orbit
        if (!this.world().isRemote)
        {
            Vector3 currentPos = new Vector3();
            int blockID = -1;
            int metadata = -1;
            double dist = -1;
            int takenBlocks = 0;
            Block block = null;

            /** Block removal loop */
            loop:
            for (int radius = 1; radius < this.getRadius(); radius++)
            {
                for (int xCoord = -radius; xCoord < radius; xCoord++)
                {
                    for (int yCoord = -radius; yCoord < radius; yCoord++)
                    {
                        for (int zCoord = -radius; zCoord < radius; zCoord++)
                        {
                            currentPos.x = position.x + xCoord;
                            currentPos.y = position.y + yCoord;
                            currentPos.z = position.z + zCoord;

                            dist = MathHelper.sqrt_double((xCoord * xCoord + yCoord * yCoord + zCoord * zCoord));

                            if (dist > radius || dist < radius - 2)
                                continue;

                            blockID = currentPos.getBlockID(this.world());
                            metadata = currentPos.getBlockMetadata(this.world());
                            block = Block.blocksList[blockID];

                            if (block != null && block.getBlockHardness(this.world(), currentPos.intX(), currentPos.intY(), currentPos.intZ()) >= 0)
                            {
                                if (block instanceof IForceFieldBlock)
                                {
                                    ((IForceFieldBlock) block).weakenForceField(this.world(), currentPos.intX(), currentPos.intY(), currentPos.intZ(), 50);
                                    continue;
                                }

                                this.world().setBlock(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0, 0, block instanceof BlockFluid ? 0 : 2);
                                //TODO: render fluid streams
                                if (block instanceof BlockFluid || block instanceof IFluidBlock)
                                    continue;

                                currentPos.add(0.5D);

                                if (this.world().rand.nextFloat() > 0.8)
                                {
                                    EntityFlyingBlock entity = new EntityFlyingBlock(this.world(), currentPos, blockID, metadata);
                                    this.world().spawnEntityInWorld(entity);
                                    entity.yawChange = 50 * this.world().rand.nextFloat();
                                    entity.pitchChange = 50 * this.world().rand.nextFloat();
                                }

                                takenBlocks++;
                                if (takenBlocks > this.maxTakeBlocks)
                                    break loop;
                            }
                        }
                    }
                }
            }
        }

        /** Entity orbital removal & movement loop */
        float radius = this.getRadius() + this.getRadius() / 2;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<Entity> allEntities = this.world().getEntitiesWithinAABB(Entity.class, bounds);
        boolean doExplosion = true;

        for (Entity entity : allEntities)
        {
            doExplosion = !this.affectEntity(radius, entity, doExplosion);
        }
        /*
         * if (this.worldObj.isRemote) { for (int i = 0; i < 10 * (2 -
         * ZhuYaoZhaPin.proxy.getParticleSetting()); i++) { Vector3 randomVector = new
         * Vector3(this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius(),
         * this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius(),
         * this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius());
         * ZhuYaoZhaPin.proxy.spawnParticle("smoke", this.worldObj, Vector3.add(this.position,
         * randomVector), 0, 0, 0, 1, 1, 1, 7.0F, 8); }
         * List<Entity> list = ZhuYaoZhaPin.proxy.getEntityFXs();
         * if (list != null) { for (Entity entity : list) { if (this.position.distanceTo(new
         * Vector3(entity)) <= radius) { this.affectEntity(radius, entity, false); } } } }
         */

        if (this.world().rand.nextInt(8) == 0)
        {
            this.world().playSoundEffect(position.x + (Math.random() - 0.5) * radius, position.y + (Math.random() - 0.5) * radius, position.z + (Math.random() - 0.5) * radius, Reference.PREFIX + "collapse", 6.0F - this.world().rand.nextFloat(), 1.0F - this.world().rand.nextFloat() * 0.4F);
        }

        this.world().playSoundEffect(position.x, position.y, position.z, Reference.PREFIX + "redmatter", 3.0F, (1.0F + (this.world().rand.nextFloat() - this.world().rand.nextFloat()) * 0.2F) * 1F);
    }

    /** Makes an entity get affected by Red Matter.
     * 
     * @Return True if explosion happened */
    public boolean affectEntity(float radius, Entity entity, boolean doExplosion)
    {
        boolean explosionCreated = false;

        if (entity == this.controller)
        {
            return false;
        }

        if (entity instanceof IExplosiveIgnore)
        {
            if (((IExplosiveIgnore) entity).canIgnore(this))
            {
                return false;
            }
        }

        if (entity instanceof EntityPlayer)
        {
            if (((EntityPlayer) entity).capabilities.isCreativeMode)
            {
                return false;
            }
        }

        double xDifference = entity.posX - position.x;
        double yDifference = entity.posY - position.y;
        double zDifference = entity.posZ - position.z;

        /** The percentage of the closeness of the entity. */
        double xPercentage = 1 - (xDifference / radius);
        double yPercentage = 1 - (yDifference / radius);
        double zPercentage = 1 - (zDifference / radius);
        double distancePercentage = (this.position.distance(new Vector3(entity)) / radius);

        Vector3 entityPosition = new Vector3(entity);
        Vector3 centeredPosition = entityPosition.clone().subtract(this.position);
        centeredPosition.rotate(1.5 * distancePercentage * Math.random(), 1.5 * distancePercentage * Math.random(), 1.5 * distancePercentage * Math.random());
        Vector3 newPosition = Vector3.translate(this.position, centeredPosition);
        // Orbit Velocity
        entity.addVelocity(newPosition.x - entityPosition.x, 0, newPosition.z - entityPosition.z);
        // Gravity Velocity
        entity.addVelocity(-xDifference * 0.015 * xPercentage, -yDifference * 0.015 * yPercentage, -zDifference * 0.015 * zPercentage);

        if (this.world().isRemote)
        {
            if (entity instanceof EntityFlyingBlock)
            {
                if (ICBMExplosion.proxy.getParticleSetting() == 0)
                {
                    if (this.world().rand.nextInt(5) == 0)
                    {
                        ICBMExplosion.proxy.spawnParticle("digging", this.world(), new Vector3(entity), -xDifference, -yDifference + 10, -zDifference, ((EntityFlyingBlock) entity).blockID, 0, ((EntityFlyingBlock) entity).metadata, 2, 1);

                    }
                }
            }
        }

        if (Vector3.distance(new Vector3(entity.posX, entity.posY, entity.posZ), position) < 4)
        {
            if (doExplosion && !explosionCreated && callCount % 5 == 0)
            {
                /** Inject velocities to prevent this explosion to move RedMatter. */
                Vector3 tempMotion = new Vector3(this.controller.motionX, this.controller.motionY, this.controller.motionZ);
                this.world().createExplosion(this.exploder, entity.posX, entity.posY, entity.posZ, 3.0F, true);
                this.controller.motionX = tempMotion.x;
                this.controller.motionY = tempMotion.y;
                this.controller.motionZ = tempMotion.z;
                explosionCreated = true;
            }

            if (entity instanceof EntityLiving)
            {
                entity.fallDistance = 0;
            }
            else
            {
                if (entity instanceof EntityExplosion)
                {
                    if (((EntityExplosion) entity).blast instanceof BlastAntimatter || ((EntityExplosion) entity).blast instanceof BlastRedmatter)
                    {
                        this.world().playSoundEffect(position.x, position.y, position.z, Reference.PREFIX + "explosion", 7.0F, (1.0F + (this.world().rand.nextFloat() - this.world().rand.nextFloat()) * 0.2F) * 0.7F);

                        if (this.world().rand.nextFloat() > 0.85 && !this.world().isRemote)
                        {
                            entity.setDead();
                            return explosionCreated;
                        }
                    }
                }
                else if (entity instanceof EntityExplosive)
                {
                    ((EntityExplosive) entity).explode();
                }
                else
                {
                    entity.setDead();
                }
            }
        }

        return explosionCreated;
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
    public long getEnergy()
    {
        return -3000;
    }

    @Override
    public boolean isMovable()
    {
        return this.callCount > 1;
    }
}
