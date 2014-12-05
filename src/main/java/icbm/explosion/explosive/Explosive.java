package icbm.explosion.explosive;

import icbm.Reference;
import icbm.api.explosion.IExplosive;
import icbm.explosion.blast.BlastBasic;
import resonant.lib.type.Pair;
import resonant.lib.world.IWorldChangeAction;
import icbm.api.explosion.TriggerCause;
import icbm.explosion.Blast;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

/**
 * Created by robert on 11/19/2014.
 */
public class Explosive implements IExplosive
{
    protected String unlocalizedName;
    protected Class<? extends Blast> blastClass;
    int multiplier = 1;

    public Explosive(Class<? extends Blast> blastClass)
    {
        this(blastClass.getSimpleName(), blastClass, 1);
    }

    public Explosive(String name, Class<? extends Blast> blastClass)
    {
        this(name, blastClass, 1);
    }

    public Explosive(String name, Class<? extends Blast> blastClass, int multiplier)
    {
        this.unlocalizedName = name;
        this.blastClass = blastClass;
        this.multiplier = multiplier;
    }

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, int yieldMultiplier)
    {
        try
        {
            return blastClass.newInstance().setLocation(world, (int)x, (int)y, (int)z).setYield(yieldMultiplier * multiplier).setCause(triggerCause);
        }
        catch (InstantiationException e)
        {
            Reference.LOGGER.log(Level.ERROR, "Failed to create blast object");
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            Reference.LOGGER.log(Level.ERROR, "Failed to create blast object");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Pair<Integer, Integer> getEstimatedRange(TriggerCause triggerCause, int yieldMultiplier)
    {
        return new Pair<Integer, Integer>(yieldMultiplier * multiplier, (yieldMultiplier * multiplier) * 2);
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
