package icbm.explosion.explosive.thread;

import java.util.HashSet;

import net.minecraft.entity.Entity;
import resonant.lib.tranform.Vector3;
import resonant.lib.tranform.VectorWorld;

/** @author Calclavia */
public abstract class ThreadExplosion extends Thread
{
    public final VectorWorld position;
    public int radius;
    public float energy;
    public Entity source;

    public boolean isComplete = false;

    public final HashSet<Vector3> deltaSet = new HashSet<Vector3>();
    public final HashSet<Vector3> results = new HashSet<Vector3>();

    public ThreadExplosion(VectorWorld position, int radius, float energy, Entity source)
    {
        this.position = position;
        this.radius = radius;
        this.energy = energy;
        this.source = source;
        this.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run()
    {
        this.isComplete = true;
    }
}
