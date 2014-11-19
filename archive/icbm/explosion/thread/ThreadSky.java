package icbm.explosion.thread;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;

/** Used for searching block spawn. Returns a block above this found block coordinate.
 * 
 * @author Calclavia */
public class ThreadSky extends ThreadExplosion
{
    public static interface IThreadCallBack
    {
        public float getResistance(World world, Vector3 position, Vector3 targetPosition, Entity source, Block block);
    }

    public IThreadCallBack callBack;

    public ThreadSky(VectorWorld position, int banJing, float nengLiang, Entity source, IThreadCallBack callBack)
    {
        super(position, banJing, nengLiang, source);
        this.callBack = callBack;
    }

    public ThreadSky(VectorWorld position, int banJing, float nengLiang, Entity source)
    {
        this(position, banJing, nengLiang, source, new IThreadCallBack()
        {

            @Override
            public float getResistance(World world, Vector3 explosionPosition, Vector3 targetPosition, Entity source, Block block)
            {
                float resistance = 0;

                if (block instanceof BlockLiquid || block instanceof IFluidBlock)
                {
                    resistance = 0.25f;
                }
                else
                {
                    resistance = block.getExplosionResistance(source, world, targetPosition.xi(), targetPosition.yi(), targetPosition.zi(), explosionPosition.xi(), explosionPosition.yi(), explosionPosition.zi());
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

                Vector3 targetPosition = this.position.clone();

                for (float var21 = 0.3F; power > 0f; power -= var21 * 0.75F * 10)
                {
                    if (targetPosition.distance(position) > this.radius)
                        break;

                    Block blockID = this.position.world().getBlock(targetPosition.xi(), targetPosition.yi(), targetPosition.zi());

                    if (!blockID.isAir(position.world(), targetPosition.xi(), targetPosition.yi(), targetPosition.zi()))
                    {
                        if (blockID.getBlockHardness(position.world(), targetPosition.xi(), targetPosition.yi(), targetPosition.zi()) < 0)
                        {
                            break;
                        }

                        float resistance = this.callBack.getResistance(this.position.world(), position, targetPosition, source, blockID);

                        power -= resistance;

                        if (power > 0f)
                        {
                            this.results.add(targetPosition.clone().add(new Vector3(0, 1, 0)));
                        }
                    }

                    targetPosition.addEquals(delta);
                }
            }
        }
        super.run();
    }
}
