package icbm.explosion;

import java.util.Collection;
import java.util.HashMap;

import cpw.mods.fml.common.eventhandler.Event;
import icbm.explosion.thread.ThreadExplosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import icbm.api.event.ExplosiveEvent;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveBlast;
import icbm.api.explosion.TriggerCause;
import resonant.engine.References;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;

/** Registry for all explosive which create blasts for anything from bombs to missiles */
public final class ExplosiveRegistry
{
    private static final HashMap<String, IExplosive> idToExplosiveMap = new HashMap<String, IExplosive>();

    /**
     * Registers or gets an instanceof of explosive
     * @param modID - modID
     * @param ex - explosive instance
     * @return explosive instance
     */
    public static IExplosive registerOrGetExplosive(String modID, IExplosive ex)
    {
        if (!isRegistered(ex))
        {
            idToExplosiveMap.put(ex.getUnlocalizedName(), ex);
            ex.onRegistered();
            References.LOGGER.info("ExplosiveRegistry> Mod: " + modID + "  Registered explosive instance " + ex);
            return ex;
        }
        else
        {
            return get(ex.getUnlocalizedName());
        }
    }

    public static TriggerResult triggerExplosive(World world, double x, double y, double z, IExplosive ex, TriggerCause triggerCause, int multi)
    {
        IExplosiveBlast blast = createBlastForTrigger(world, x, y, z, ex, triggerCause, multi);
        VectorWorld loc = new VectorWorld(world, x, y, z);
        if(blast != null)
        {
            Event event = new ExplosiveEvent.OnBlastCreatedEvent(world, loc, ex, triggerCause, blast);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled())
            {
                blast.doEffectOther(world, x, y, z, triggerCause);
                if(blast.shouldThreadExplosion(triggerCause))
                {
                    ThreadExplosion thread = new ThreadExplosion(loc, blast, ex, triggerCause);
                    thread.start();
                }
                else
                {
                   _doBlast(loc, ex, triggerCause, blast);
                }

                return TriggerResult.TRIGGERED;
            }
            return TriggerResult.BLAST_BLOCKED;
        }
        return TriggerResult.NO_BLAST;
    }

    public static TriggerResult _doBlast(VectorWorld vec, IExplosive ex, TriggerCause triggerCause, IExplosiveBlast blast)
    {
        Collection<Vector3> effectedBlocks = blast.getEffectedBlocks(triggerCause);
        Event event = new ExplosiveEvent.BlocksEffectedExplosiveEvent(vec, ex, triggerCause, blast, effectedBlocks);
        MinecraftForge.EVENT_BUS.post(event);
        if(effectedBlocks != null && !effectedBlocks.isEmpty())
        {
            for(Vector3 v : effectedBlocks)
            {
                blast.doEffectBlock(v, triggerCause);
            }
        }
        return TriggerResult.TRIGGERED;
    }

    public static IExplosiveBlast createBlastForTrigger(World world, double x, double y, double z, IExplosive ex, TriggerCause triggerCause, int multi)
    {
        if(isRegistered(ex))
        {
            ExplosiveEvent.OnExplosiveTriggeredEvent event = new ExplosiveEvent.OnExplosiveTriggeredEvent(world, new Vector3(x, y, z), ex, triggerCause);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled())
            {
                return ex.createBlastForTrigger(world, x, y, z, triggerCause, multi);
            }
        }
        return null;
    }

    public static boolean isRegistered(IExplosive explosive)
    {
        return idToExplosiveMap.containsKey(explosive.getUnlocalizedName());
    }

    public static IExplosive get(String name)
    {
        return idToExplosiveMap.get(name);
    }

    public static Collection<IExplosive> getExplosives()
    {
        return idToExplosiveMap.values();
    }

    public static HashMap<String, IExplosive> getExplosiveMap()
    {
        return idToExplosiveMap;
    }

    public enum TriggerResult
    {
        TRIGGERED,
        NO_BLAST,
        BLAST_BLOCKED;
    }

}
