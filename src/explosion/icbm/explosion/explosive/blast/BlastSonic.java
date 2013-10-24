package icbm.explosion.explosive.blast;

import icbm.core.ICBMCore;
import icbm.explosion.EntityFlyingBlock;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.ExplosionBase;
import icbm.explosion.explosive.thread.ThreadLargeExplosion;
import icbm.explosion.explosive.thread.ThreadLargeExplosion.IThreadCallBack;
import icbm.explosion.missile.BlockExplosive;
import icbm.explosion.missile.TileEntityExplosive;
import icbm.explosion.missile.missile.EntityMissile;

import java.util.Iterator;
import java.util.List;

import mffs.api.IForceFieldBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import universalelectricity.core.vector.Vector3;

public class BlastSonic extends ExplosionBase
{
    private float nengLiang;
    private ThreadLargeExplosion thread;
    private boolean hasShockWave = false;;

    public BlastSonic(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastSonic(World world, Entity entity, double x, double y, double z, float size, float nengLiang)
    {
        this(world, entity, x, y, z, size);
        this.nengLiang = nengLiang;
    }

    public ExplosionBase setShockWave()
    {
        this.hasShockWave = true;
        return this;
    }

    @Override
    public void doPreExplode()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.hasShockWave)
            {
                for (int x = (int) (-this.getRadius() * 2); x < this.getRadius() * 2; ++x)
                {
                    for (int y = (int) (-this.getRadius() * 2); y < this.getRadius() * 2; ++y)
                    {
                        for (int z = (int) (-this.getRadius() * 2); z < this.getRadius() * 2; ++z)
                        {
                            Vector3 targetPosition = Vector3.add(position, new Vector3(x, y, z));
                            int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                            if (blockID > 0)
                            {
                                Material material = worldObj.getBlockMaterial(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                                if (blockID != Block.bedrock.blockID && !(Block.blocksList[blockID] instanceof BlockFluid) && (Block.blocksList[blockID].getExplosionResistance(this.exploder, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ()) > this.nengLiang || material == Material.glass))
                                {
                                    targetPosition.setBlock(worldObj, 0);
                                }
                            }
                        }
                    }
                }
            }

            this.thread = new ThreadLargeExplosion(this.worldObj, position, (int) this.getRadius(), this.nengLiang, this.exploder, new IThreadCallBack()
            {
                @Override
                public float getResistance(World world, Vector3 explosionPosition, Vector3 targetPosition, Entity source, Block block)
                {
                    float resistance = 0;

                    if (block instanceof BlockFluid || block instanceof IFluidBlock)
                    {
                        resistance = 1f;
                    }
                    else
                    {
                        resistance = block.getExplosionResistance(source, world, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), explosionPosition.intX(), explosionPosition.intY(), explosionPosition.intZ());
                    }

                    return resistance;
                }

            });
            this.thread.start();
        }

        if (this.hasShockWave)
        {
            this.worldObj.playSoundEffect(position.x, position.y, position.z, ICBMCore.PREFIX + "hypersonic", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        }
        else
        {
            this.worldObj.playSoundEffect(position.x, position.y, position.z, ICBMCore.PREFIX + "sonicwave", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    @Override
    public void doExplode()
    {
        int r = this.callCount;

        if (!this.worldObj.isRemote)
        {
            if (this.thread != null && this.thread.isComplete)
            {
                Iterator<Vector3> it = this.thread.results.iterator();

                while (it.hasNext())
                {
                    Vector3 targetPosition = it.next();
                    double distance = Vector3.distance(targetPosition, position);

                    if (distance > r || distance < r - 3)
                        continue;

                    int blockID = this.worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                    if (blockID == 0 || blockID == Block.bedrock.blockID || blockID == Block.obsidian.blockID)
                        continue;

                    if (Block.blocksList[blockID] instanceof IForceFieldBlock)
                        continue;

                    int metadata = this.worldObj.getBlockMetadata(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                    if (distance < r - 1 || this.worldObj.rand.nextInt(3) > 0)
                    {
                        if (blockID == ICBMExplosion.blockExplosive.blockID)
                        {
                            BlockExplosive.yinZha(this.worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), ((TileEntityExplosive) this.worldObj.getBlockTileEntity(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ())).haoMa, 1);
                        }
                        else
                        {
                            this.worldObj.setBlockToAir(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
                        }

                        targetPosition.translate(0.5D);

                        if (this.worldObj.rand.nextFloat() < 0.3 * (this.getRadius() - r))
                        {
                            EntityFlyingBlock entity = new EntityFlyingBlock(this.worldObj, targetPosition, blockID, metadata);
                            this.worldObj.spawnEntityInWorld(entity);
                            entity.yawChange = 50 * this.worldObj.rand.nextFloat();
                            entity.pitchChange = 100 * this.worldObj.rand.nextFloat();
                        }

                        it.remove();
                    }
                }
            }
        }

        int radius = 2 * this.callCount;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<Entity> allEntities = this.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

        synchronized (allEntities)
        {
            for (Iterator it = allEntities.iterator(); it.hasNext();)
            {
                Entity entity = (Entity) it.next();

                if (entity instanceof EntityMissile)
                {
                    ((EntityMissile) entity).setExplode();
                    break;
                }
                else
                {
                    double xDifference = entity.posX - position.x;
                    double zDifference = entity.posZ - position.z;

                    r = (int) this.getRadius();
                    if (xDifference < 0)
                        r = (int) -this.getRadius();

                    entity.motionX += (r - xDifference) * 0.02 * this.worldObj.rand.nextFloat();
                    entity.motionY += 3 * this.worldObj.rand.nextFloat();

                    r = (int) this.getRadius();
                    if (zDifference < 0)
                        r = (int) -this.getRadius();

                    entity.motionZ += (r - zDifference) * 0.02 * this.worldObj.rand.nextFloat();
                }
            }
        }

        if (this.callCount > this.getRadius())
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
        return 4;
    }

    @Override
    public float getEnergy()
    {
        return 3000;
    }
}
