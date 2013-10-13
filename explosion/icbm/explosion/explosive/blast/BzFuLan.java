package icbm.explosion.explosive.blast;

import icbm.core.ICBMCore;
import icbm.explosion.explosive.ExplosionBase;
import icbm.explosion.explosive.thread.ThreadLargeExplosion;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import universalelectricity.core.vector.Vector3;

/** Creates radiation spawning
 * 
 * @author Calclavia */
public class BzFuLan extends ExplosionBase
{
    private ThreadLargeExplosion thread;
    private float nengLiang;

    public BzFuLan(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BzFuLan(World world, Entity entity, double x, double y, double z, float size, float nengLiang)
    {
        this(world, entity, x, y, z, size);
        this.nengLiang = nengLiang;
    }

    @Override
    public void doPreExplode()
    {
        if (!this.worldObj.isRemote)
        {
            this.thread = new ThreadLargeExplosion(worldObj, position, (int) this.getRadius(), this.nengLiang, this.exploder);
            this.thread.start();
        }
    }

    @Override
    public void doExplode()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.thread.isComplete)
            {
                for (Vector3 targetPosition : this.thread.results)
                {
                    /** Decay the blocks. */
                    int blockID = targetPosition.getBlockID(this.worldObj);

                    if (blockID > 0)
                    {
                        if (blockID == Block.grass.blockID || blockID == Block.sand.blockID)
                        {
                            if (this.worldObj.rand.nextFloat() > 0.96)
                            {
                                targetPosition.setBlock(this.worldObj, ICBMCore.blockRadioactive.blockID);
                            }
                        }

                        if (blockID == Block.stone.blockID)
                        {
                            if (this.worldObj.rand.nextFloat() > 0.99)
                            {
                                targetPosition.setBlock(this.worldObj, ICBMCore.blockRadioactive.blockID);
                            }
                        }

                        else if (blockID == Block.leaves.blockID)
                        {
                            targetPosition.setBlock(this.worldObj, 0);
                        }
                        else if (blockID == Block.tallGrass.blockID)
                        {
                            if (Math.random() * 100 > 50)
                            {
                                targetPosition.setBlock(this.worldObj, Block.cobblestone.blockID);
                            }
                            else
                            {
                                targetPosition.setBlock(this.worldObj, 0);
                            }
                        }
                        else if (blockID == Block.tilledField.blockID)
                        {
                            targetPosition.setBlock(this.worldObj, ICBMCore.blockRadioactive.blockID);
                        }
                        else if (blockID == Block.waterStill.blockID || blockID == Block.waterMoving.blockID)
                        {
                            if (FluidRegistry.getFluid("toxicwaste") != null)
                            {
                                targetPosition.setBlock(this.worldObj, FluidRegistry.getFluid("toxicwaste").getBlockID());
                            }
                        }
                    }
                }

                this.controller.endExplosion();
            }
        }
    }

    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public float getEnergy()
    {
        return 100;
    }
}
