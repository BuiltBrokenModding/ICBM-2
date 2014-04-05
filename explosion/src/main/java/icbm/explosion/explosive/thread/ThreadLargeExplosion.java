package icbm.explosion.explosive.thread;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

/** Used for large raycasting explosions.
 * 
 * @author Calclavia */
public class ThreadLargeExplosion extends ThreadExplosion
{
    public static interface IThreadCallBack
    {
        public float getResistance(World world, Vector3 position, Vector3 targetPosition, Entity source, Block block);
    }

    public IThreadCallBack callBack;

    public ThreadLargeExplosion(VectorWorld position, int range, float energy, Entity source, IThreadCallBack callBack)
    {
        super(position, range, energy, source);
        this.callBack = callBack;
    }

    public ThreadLargeExplosion(VectorWorld position, int banJing, float nengLiang, Entity source)
    {
        this(position, banJing, nengLiang, source, new IThreadCallBack()
        {

            @Override
            public float getResistance(World world, Vector3 explosionPosition, Vector3 targetPosition, Entity source, Block block)
            {
                float resistance = 0;

                if (block instanceof BlockFluid || block instanceof IFluidBlock)
                {
                    resistance = 0.25f;
                }
                else
                {
                    resistance = block.getExplosionResistance(source, world, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), explosionPosition.intX(), explosionPosition.intY(), explosionPosition.intZ());
                }

                return resistance;
            }

        });
    }

    @Override
    public void run()
    {
        int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / this.radius));

        for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
        {
            for (int theta_n = 0; theta_n < steps; theta_n++)
            {
                double phi = Math.PI * 2 / steps * phi_n;
                double theta = Math.PI / steps * theta_n;

                Vector3 delta = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
                float power = this.energy - (this.energy * this.position.world().rand.nextFloat() / 2);

                Vector3 targetPosition = position.clone();

                for (float d = 0.3F; power > 0f; power -= d * 0.75F * 10)
                {
                    if (targetPosition.distance(position) < this.radius)
                        break;

                    Block block = Block.blocksList[this.position.world().getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ())];

                    if (block != null)
                    {
                        if (block.getBlockHardness(this.position.world(), targetPosition.intX(), targetPosition.intY(), targetPosition.intZ()) >= 0)
                        {
                            power -= this.callBack.getResistance(this.position.world(), position, targetPosition, source, block);

                            if (power > 0f)
                            {
                                this.results.add(targetPosition.clone());
                            }

                        }
                    }
                    targetPosition.translate(delta);
                }
            }
        }

        super.run();
    }
}
