package icbm.explosion.thread;

import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveBlast;
import icbm.api.explosion.TriggerCause;
import net.minecraft.world.World;
import resonant.lib.transform.vector.VectorWorld;

public class ThreadExplosion extends Thread
{
    public final VectorWorld position;
    public final IExplosiveBlast blast;
    public final IExplosive ex;
    public final TriggerCause triggerCause;

    public boolean isComplete = false;

    public ThreadExplosion(VectorWorld vec, IExplosiveBlast blast, IExplosive ex, TriggerCause triggerCause)
    {
        this.position = vec;
        this.blast = blast;
        this.ex = ex;
        this.triggerCause = triggerCause;
        this.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run()
    {
        this.isComplete = true;
    }
}