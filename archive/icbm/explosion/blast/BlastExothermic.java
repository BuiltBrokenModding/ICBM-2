package icbm.explosion.blast;

import icbm.Reference;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import resonant.lib.transform.vector.Vector3;

public class BlastExothermic extends BlastBeam
{
    public BlastExothermic(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        this.red = 0.7f;
        this.green = 0.3f;
        this.blue = 0;
    }

    @Override
    public void doExplode()
    {
        super.doExplode();
        this.world().playSoundEffect(position.x(), position.y(), position.z(), Reference.PREFIX + "beamcharging", 4.0F, 0.8F);
    }

    @Override
    public void doPostExplode()
    {
        super.doPostExplode();

        if (!this.world().isRemote)
        {
            this.world().playSoundEffect(position.x(), position.y(), position.z(), Reference.PREFIX + "powerdown", 4.0F, 0.8F);

            if (this.canFocusBeam(this.world(), position) && this.thread.isComplete)
            {
                for (Vector3 targetPosition : this.thread.results)
                {
                    double distance = targetPosition.subtract(position).magnitude();

                    double distanceFromCenter = position.distance(targetPosition);

                    if (distanceFromCenter > this.getRadius())
                        continue;

                    /*
                     * Reduce the chance of setting blocks on fire based on distance from center.
                     */
                    double chance = this.getRadius() - (Math.random() * distanceFromCenter);

                    if (chance > distanceFromCenter * 0.55)
                    {
                        /*
                         * Check to see if the block is an air block and there is a block below it
                         * to support the fire.
                         */
                        Block blockID = this.world().getBlock(targetPosition.xi(), targetPosition.yi(), targetPosition.zi());

                        if (blockID == Blocks.water || blockID == Blocks.flowing_water || blockID == Blocks.ice)
                        {
                            this.world().setBlockToAir(targetPosition.xi(), targetPosition.yi(), targetPosition.zi());
                        }

                        if ((blockID != null || blockID == Blocks.snow))
                        // getBlockMaterial has been [de]obfuscated and is now known as func_149688_o
                        {
                            if (this.world().rand.nextFloat() > 0.999)
                            {
                                this.world().setBlock(targetPosition.xi(), targetPosition.yi(), targetPosition.zi(), Blocks.flowing_lava, 0, 2);
                            }
                            else
                            {
                                this.world().setBlock(targetPosition.xi(), targetPosition.yi(), targetPosition.zi(), Blocks.fire, 0, 2);

                                blockID = this.world().getBlock(targetPosition.xi(), targetPosition.yi() - 1, targetPosition.zi());

                                if (/*((ExExothermic) Explosive.exothermic).createNetherrack && */ (blockID == Blocks.stone || blockID == Blocks.grass || blockID == Blocks.dirt) && this.world().rand.nextFloat() > 0.75)
                                {
                                    this.world().setBlock(targetPosition.xi(), targetPosition.yi() - 1, targetPosition.zi(), Blocks.netherrack, 0, 2);
                                }
                            }
                        }
                        else if (blockID == Blocks.ice)
                        {
                            this.world().setBlockToAir(targetPosition.xi(), targetPosition.yi(), targetPosition.zi());
                        }
                    }
                }

                this.world().playSoundEffect(position.x() + 0.5D, position.y() + 0.5D, position.z() + 0.5D, Reference.PREFIX + "explosionfire", 6.0F, (1.0F + (world().rand.nextFloat() - world().rand.nextFloat()) * 0.2F) * 1F);
            }

            this.world().setWorldTime(18000);
        }
    }

    @Override
    public boolean canFocusBeam(World worldObj, Vector3 position)
    {
        long worldTime = worldObj.getWorldTime();

        while (worldTime > 23999)
        {
            worldTime -= 23999;
        }

        return worldTime < 12000 && !worldObj.isRaining() && super.canFocusBeam(worldObj, position);
    }

}
