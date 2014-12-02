package icbm.explosion.explosive;

import icbm.Reference;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveBlast;
import icbm.api.explosion.TriggerCause;
import icbm.explosion.Blast;
import icbm.explosion.blast.BlastBasic;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

/**
 * Created by robert on 11/19/2014.
 */
public class Explosive implements IExplosive
{
    protected String unlocalizedName;
    protected Class<? extends Blast> blastClass;

    public Explosive(String name, Class<? extends Blast> blastClass)
    {
        this.unlocalizedName = name;
        this.blastClass = blastClass;
    }

    @Override
    public IExplosiveBlast tryToTriggerExplosion(World world, double x, double y, double z, TriggerCause triggerCause, int yieldMultiplier)
    {
        try
        {
            return blastClass.newInstance();
        }
        catch (InstantiationException e)
        {
            Reference.LOGGER.log(Level.ERROR, "Failed to create blast object");
            Reference.LOGGER.catching(Level.ERROR, e);
        }
        catch (IllegalAccessException e)
        {
            Reference.LOGGER.log(Level.ERROR, "Failed to create blast object");
            Reference.LOGGER.catching(Level.ERROR, e);
        }
        return null;
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
