package icbm.explosion.explosive.thread;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import universalelectricity.core.vector.Vector3;

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

    public ThreadLargeExplosion(World world, Vector3 position, int banJing, float nengLiang, Entity source, IThreadCallBack callBack)
    {
        super(world, position, banJing, nengLiang, source);
        this.callBack = callBack;
    }

    public ThreadLargeExplosion(World world, Vector3 position, int banJing, float nengLiang, Entity source)
    {
        this(world, position, banJing, nengLiang, source, new IThreadCallBack()
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
        int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / this.banJing));

        for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
        {
            for (int theta_n = 0; theta_n < steps; theta_n++)
            {
                double phi = Math.PI * 2 / steps * phi_n;
                double theta = Math.PI / steps * theta_n;

                Vector3 delta = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
                float power = this.nengLiang - (this.nengLiang * this.world.rand.nextFloat() / 2);

                Vector3 targetPosition = position.clone();

                for (float var21 = 0.3F; power > 0f; power -= var21 * 0.75F * 10)
                {
                    if (targetPosition.distance(position) > this.banJing)
                        break;

                    int blockID = this.world.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                    if (blockID > 0)
                    {
                        if (blockID == Block.bedrock.blockID)
                        {
                            break;
                        }

                        float resistance = this.callBack.getResistance(this.world, position, targetPosition, source, Block.blocksList[blockID]);

                        power -= resistance;

                        if (power > 0f)
                        {
                            this.results.add(targetPosition.clone());
                        }
                    }

                    targetPosition.x += delta.x;
                    targetPosition.y += delta.y;
                    targetPosition.z += delta.z;
                }
            }
        }

        super.run();
    }
}
