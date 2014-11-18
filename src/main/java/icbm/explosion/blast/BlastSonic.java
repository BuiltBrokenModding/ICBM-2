package icbm.explosion.blast;

import icbm.Reference;
import icbm.content.entity.EntityFlyingBlock;
import icbm.content.entity.EntityMissile;
import icbm.content.tile.ex.TileExplosive;
import icbm.explosion.thread.ThreadLargeExplosion;
import icbm.explosion.thread.ThreadLargeExplosion.IThreadCallBack;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;

public class BlastSonic extends Blast
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

    public Blast setShockWave()
    {
        this.hasShockWave = true;
        return this;
    }

    @Override
    public void doPreExplode()
    {
        if (!this.world().isRemote)
        {
            if (this.hasShockWave)
            {
                for (int x = (int) (-this.getRadius() * 2); x < this.getRadius() * 2; ++x)
                {
                    for (int y = (int) (-this.getRadius() * 2); y < this.getRadius() * 2; ++y)
                    {
                        for (int z = (int) (-this.getRadius() * 2); z < this.getRadius() * 2; ++z)
                        {
                            VectorWorld targetPosition = position.add(new Vector3(x, y, z));
                            Block blockID = targetPosition.getBlock();

                            if (blockID != null)
                            {
                                Material material = blockID.getMaterial();

                                if (targetPosition.getHardness() >= 0 && !(blockID instanceof BlockLiquid) && (blockID.getExplosionResistance(this.exploder, world(), targetPosition.xi(), targetPosition.yi(), targetPosition.zi(), position.xi(), position.yi(), position.zi()) > this.nengLiang || material == Material.glass))
                                {
                                    targetPosition.setBlockToAir();
                                }
                            }
                        }
                    }
                }
            }

            this.thread = new ThreadLargeExplosion(this.position, (int) this.getRadius(), this.nengLiang, this.exploder, new IThreadCallBack()
            {
                @Override
                public float getResistance(World world, Vector3 explosionPosition, Vector3 targetPosition, Entity source, Block block)
                {
                    float resistance = 0;

                    if (block instanceof BlockLiquid || block instanceof IFluidBlock)
                    {
                        resistance = 1f;
                    }
                    else
                    {
                        resistance = block.getExplosionResistance(source, world, targetPosition.xi(), targetPosition.yi(), targetPosition.zi(), explosionPosition.xi(), explosionPosition.yi(), explosionPosition.zi());
                    }

                    return resistance;
                }

            });
            this.thread.start();
        }

        if (this.hasShockWave)
        {
            this.world().playSoundEffect(position.x(), position.y(), position.z(), Reference.PREFIX + "hypersonic", 4.0F, (1.0F + (this.world().rand.nextFloat() - this.world().rand.nextFloat()) * 0.2F) * 0.7F);
        }
        else
        {
            this.world().playSoundEffect(position.x(), position.y(), position.z(), Reference.PREFIX + "sonicwave", 4.0F, (1.0F + (this.world().rand.nextFloat() - this.world().rand.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    @Override
    public void doExplode()
    {
        int r = this.callCount;

        if (!this.world().isRemote)
        {
            if (this.thread != null && this.thread.isComplete)
            {
                Iterator<Vector3> it = this.thread.results.iterator();

                while (it.hasNext())
                {
                    Vector3 targetPosition = it.next();
                    double distance = targetPosition.add(position).magnitude();

                    if (distance > r || distance < r - 3)
                        continue;

                    Block blockID = targetPosition.getBlock(world());
                    TileEntity ent = targetPosition.getTileEntity(world());
                    double hardness = targetPosition.getHardness(world());

                    if (hardness < 0 || hardness > 10000)
                        continue;

                    //if (Block.blocksList[blockID] instanceof IForceFieldBlock)
                    //    continue;

                    int metadata = this.world().getBlockMetadata(targetPosition.xi(), targetPosition.yi(), targetPosition.zi());

                    if (distance < r - 1 || this.world().rand.nextInt(3) > 0)
                    {
                        if (ent instanceof TileExplosive)
                        {
                            ((TileExplosive)ent).explode(1);
                        }
                        else
                        {
                            this.world().setBlockToAir(targetPosition.xi(), targetPosition.yi(), targetPosition.zi());
                        }

                        targetPosition.add(0.5D);

                        if (this.world().rand.nextFloat() < 0.3 * (this.getRadius() - r))
                        {
                            EntityFlyingBlock entity = new EntityFlyingBlock(this.world(), targetPosition, blockID, metadata);
                            this.world().spawnEntityInWorld(entity);
                            entity.yawChange = 50 * this.world().rand.nextFloat();
                            entity.pitchChange = 100 * this.world().rand.nextFloat();
                        }

                        it.remove();
                    }
                }
            }
        }

        int radius = 2 * this.callCount;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x() - radius, position.y() - radius, position.z() - radius, position.x() + radius, position.y() + radius, position.z() + radius);
        List<Entity> allEntities = this.world().getEntitiesWithinAABB(Entity.class, bounds);

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
                    double xDifference = entity.posX - position.x();
                    double zDifference = entity.posZ - position.z();

                    r = (int) this.getRadius();
                    if (xDifference < 0)
                        r = (int) -this.getRadius();

                    entity.motionX += (r - xDifference) * 0.02 * this.world().rand.nextFloat();
                    entity.motionY += 3 * this.world().rand.nextFloat();

                    r = (int) this.getRadius();
                    if (zDifference < 0)
                        r = (int) -this.getRadius();

                    entity.motionZ += (r - zDifference) * 0.02 * this.world().rand.nextFloat();
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
    public long getEnergy()
    {
        return 3000;
    }
}
