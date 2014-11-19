package icbm.explosion;

import java.util.Collection;
import java.util.HashMap;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import icbm.api.event.ExplosiveEvent;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveBlast;
import icbm.api.explosion.TriggerCause;
import resonant.engine.References;
import resonant.lib.transform.vector.Vector3;

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
        IExplosiveBlast blast = tryToTriggerExplosion(world, x, y, z, ex, triggerCause, multi);
        if(blast != null)
        {
            Event event = new ExplosiveEvent.OnBlastCreatedEvent(world, new Vector3(x, y, z), ex, triggerCause, blast);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled())
            {
                blast.doEffectOther(world, x, y, z, triggerCause);
                if(blast.shouldThreadExplosion(triggerCause))
                {
                    //TODO create thread for calculations and have it que to effect world
                    //Make sure thread handles its own event calls
                }
                else
                {
                    Collection<Vector3> effectedBlocks = blast.getEffectedBlocks(triggerCause);
                    event = new ExplosiveEvent.BlocksEffectedExplosiveEvent(world, new Vector3(x, y, z), ex, triggerCause, blast, effectedBlocks);
                    MinecraftForge.EVENT_BUS.post(event);
                    if(effectedBlocks != null && !effectedBlocks.isEmpty())
                    {
                        blast.doEffectBlocks(effectedBlocks, triggerCause);
                    }
                }

                return TriggerResult.TRIGGERED;
            }
            return TriggerResult.BLAST_BLOCKED;
        }
        return TriggerResult.NO_BLAST;
    }

    public static IExplosiveBlast tryToTriggerExplosion(World world, double x, double y, double z, IExplosive ex, TriggerCause triggerCause, int multi)
    {
        if(isRegistered(ex))
        {
            ExplosiveEvent.OnExplosiveTriggeredEvent event = new ExplosiveEvent.OnExplosiveTriggeredEvent(world, new Vector3(x, y, z), ex, triggerCause);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled())
            {
                return ex.tryToTriggerExplosion(world, x, y, z, triggerCause, multi);
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
