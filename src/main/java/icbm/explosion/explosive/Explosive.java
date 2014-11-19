package icbm.explosion.explosive;

import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveBlast;
import icbm.api.explosion.TriggerCause;
import icbm.explosion.blast.BlastBasic;
import net.minecraft.world.World;

/**
 * Created by robert on 11/19/2014.
 */
public class Explosive implements IExplosive
{
    protected String unlocalizedName;

    public Explosive(String name)
    {
        unlocalizedName = name;
    }

    @Override
    public IExplosiveBlast tryToTriggerExplosion(World world, double x, double y, double z, TriggerCause triggerCause, int yieldMultiplier)
    {
        return new BlastBasic(world, (int)x, (int)y, (int)z, 5 * yieldMultiplier);
    }

    @Override
    public void onRegistered()
    {

    }

    @Override
    public String getUnlocalizedName()
    {
        return unlocalizedName;
    }

    @Override
    public String toString()
    {
        return getUnlocalizedName();
    }
}
