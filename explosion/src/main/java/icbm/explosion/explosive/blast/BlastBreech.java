package icbm.explosion.explosive.blast;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.Vector3;
import calclavia.api.mffs.IForceFieldBlock;
import calclavia.lib.prefab.tile.IRotatable;

public class BlastBreech extends BlastRepulsive
{
    private int depth;

    public BlastBreech(World world, Entity entity, double x, double y, double z, float size, int depth)
    {
        this(world, entity, x, y, z, size);
        this.depth = depth;
    }

    public BlastBreech(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        this.nengLiang = 13;
    }

    @Override
    public void doExplode()
    {
        if (!this.world().isRemote)
        {
            final Vector3 difference = new Vector3();

            if (this.exploder instanceof IRotatable)
            {
                difference.translate(((IRotatable) this.exploder).getDirection());
            }
            else
            {
                difference.translate(ForgeDirection.DOWN);
            }

            this.world().playSoundEffect(position.x, position.y, position.z, "random.explode", 5.0F, (1.0F + (world().rand.nextFloat() - world().rand.nextFloat()) * 0.2F) * 0.7F);

            for (int i = 0; i < this.depth; i++)
            {
                if (Block.blocksList[position.getBlockID(world())] != null)
                {
                    if (Block.blocksList[position.getBlockID(world())].getExplosionResistance(this.exploder, world(), position.intX(), position.intY(), position.intZ(), position.x, position.y, position.z) > Block.obsidian.getExplosionResistance(this.exploder) || Block.blocksList[position.getBlockID(world())] instanceof IForceFieldBlock)
                    {
                        break;
                    }
                }

                super.doExplode();
                this.position.add(difference);
            }
        }
    }

    @Override
    public long getEnergy()
    {
        return (super.getEnergy() * this.depth) / 2;
    }
}
