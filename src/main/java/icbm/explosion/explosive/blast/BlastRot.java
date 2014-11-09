package icbm.explosion.explosive.blast;

import icbm.core.ICBMCore;
import icbm.explosion.explosive.thread.ThreadLargeExplosion;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import resonant.lib.transform.vector.Vector3;

/** Creates radiation spawning
 * 
 * @author Calclavia */
public class BlastRot extends Blast
{
    private ThreadLargeExplosion thread;
    private float nengLiang;

    public BlastRot(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastRot(World world, Entity entity, double x, double y, double z, float size, float nengLiang)
    {
        this(world, entity, x, y, z, size);
        this.nengLiang = nengLiang;
    }

    @Override
    public void doPreExplode()
    {
        if (!this.world().isRemote)
        {
            this.thread = new ThreadLargeExplosion(this.position, (int) this.getRadius(), this.nengLiang, this.exploder);
            this.thread.start();
        }
    }

    @Override
    public void doExplode()
    {
        if (!this.world().isRemote)
        {
            if (this.thread.isComplete)
            {
                for (Vector3 targetPosition : this.thread.results)
                {
                    /** Decay the blocks. */
                    Block blockID = targetPosition.getBlock(this.world());

                    if (blockID != null)
                    {
                        if (blockID == Blocks.grass || blockID == Blocks.sand)
                        {
                            if (this.world().rand.nextFloat() > 0.96)
                            {
                                targetPosition.setBlock(this.world(), ICBMCore.blockRadioactive);
                            }
                        }

                        if (blockID == Blocks.stone)
                        {
                            if (this.world().rand.nextFloat() > 0.99)
                            {
                                targetPosition.setBlock(this.world(), ICBMCore.blockRadioactive);
                            }
                        }

                        else if (blockID == Blocks.leaves)
                        {
                            targetPosition.setBlock(this.world(), ICBMCore.blockRadioactive, 0);
                        }
                        else if (blockID == Blocks.tallgrass)
                        {
                            if (Math.random() * 100 > 50)
                            {
                                targetPosition.setBlock(this.world(), Blocks.cobblestone);
                            }
                            else 
                            {
                                targetPosition.setBlock(this.world(),ICBMCore.blockRadioactive, 0);
                            }
                        }
                        else if (blockID == Blocks.farmland)
                        {
                            targetPosition.setBlock(this.world(), ICBMCore.blockRadioactive);
                        }
                        else if (blockID == Blocks.water || blockID == Blocks.flowing_water)
                        {
                            if (FluidRegistry.getFluid("toxicwaste") != null)
                            {
                                targetPosition.setBlock(this.world(), FluidRegistry.getFluid("toxicwaste").getBlock());
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
    public long getEnergy()
    {
        return 100;
    }
}
