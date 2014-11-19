package icbm.explosion;

import net.minecraft.world.World;
import icbm.api.explosion.IExplosiveBlast;
import icbm.api.explosion.TriggerCause;
import resonant.lib.transform.vector.IVectorWorld;
import resonant.lib.transform.vector.Vector3;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class Blast implements IExplosiveBlast, IVectorWorld
{
    public World world;
    public int x, y, z;
    public int size = 1;

    public Blast(World world, int x, int y, int z, int size)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    @Override
    public boolean shouldThreadExplosion(TriggerCause triggerCause)
    {
        return false;
    }

    @Override
    public final Collection<Vector3> getEffectedBlocks(TriggerCause triggerCause)
    {
        List<Vector3> list = new LinkedList<Vector3>();
        getEffectedBlocks(triggerCause, list);
        return list;
    }

    public void getEffectedBlocks(TriggerCause triggerCause, List<Vector3> list)
    {

    }

    @Override
    public void doEffectBlocks(Collection<Vector3> blocks, TriggerCause triggerCause)
    {

    }

    @Override
    public void doEffectOther(World world, double x, double y, double z, TriggerCause triggerCause)
    {

    }

    @Override
    public World world()
    {
        return world;
    }

    @Override
    public double x()
    {
        return x;
    }

    @Override
    public double y()
    {
        return y;
    }

    @Override
    public double z()
    {
        return z;
    }


}
