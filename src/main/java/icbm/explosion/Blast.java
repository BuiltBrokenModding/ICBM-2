package icbm.explosion;

import net.minecraft.world.World;
import resonant.api.explosion.IExplosiveBlast;
import resonant.api.explosion.Trigger;
import resonant.lib.transform.vector.IVectorWorld;
import resonant.lib.transform.vector.Vector3;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class Blast implements IExplosiveBlast, IVectorWorld
{
    public World world;
    public int x, y, z;

    public Blast(World world, int x, int y, int z)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean shouldThreadExplosion(Trigger trigger)
    {
        return false;
    }

    @Override
    public final Collection<Vector3> getEffectedBlocks(Trigger trigger)
    {
        List<Vector3> list = new LinkedList<Vector3>();
        getEffectedBlocks(trigger, list);
        return list;
    }

    public void getEffectedBlocks(Trigger trigger, List<Vector3> list)
    {

    }

    @Override
    public void doEffectBlocks(Collection<Vector3> blocks, Trigger trigger)
    {

    }

    @Override
    public void doEffectOther(World world, double x, double y, double z, Trigger trigger)
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
