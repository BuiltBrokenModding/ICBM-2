package resonant.lib.world;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import icbm.api.explosion.TriggerCause;
import net.minecraftforge.common.MinecraftForge;
import resonant.lib.transform.vector.VectorWorld;
import resonant.lib.world.event.WorldChangeActionEvent;

import java.util.Collection;
import java.util.Iterator;

/**
 * Low priority Multi-thread for IWorldChangeActions
 *
 * Calculates the impact then removes X amount of blocks at the end of the world tick
 *
 * @author Darkguardsman
 */
public class ThreadWorldChangeAction extends Thread
{
    /** Location of the Blast */
    public final VectorWorld position;
    /** Blast instance */
    public final IWorldChangeAction blast;
    /** Trigger cause of the blast */
    public final TriggerCause triggerCause;
    /** Blocks per tick limiter */
    public int blocksPerTick = 20;

    /** Blocks to remove from the world */
    private Collection<Placement> effectedBlocks;

    /** Constructor, nothing should be null and blast should be created with the center equaling
     * vec param. If its isn't it will cause events triggered to return the incorrect results.
     *
     * @param vec - location of the blast, should be the center
     * @param blast - blast instance used to remove blocks and build a list
     * @param triggerCause - cause of the explosion
     */
    public ThreadWorldChangeAction(VectorWorld vec, IWorldChangeAction blast, TriggerCause triggerCause)
    {
        this.position = vec;
        this.blast = blast;
        this.triggerCause = triggerCause;
        this.setPriority(Thread.NORM_PRIORITY);
    }

    @Override
    public void run()
    {
        //Collects the init list of blocks from the blast
        effectedBlocks = blast.getEffectedBlocks();

        //Triggers an event allowing other mods to edit the block list
        MinecraftForge.EVENT_BUS.post(new WorldChangeActionEvent.FinishedCalculatingEffectEvent(position,effectedBlocks, blast, triggerCause));

        //If we have blocks to edit then register with the event handler
        if(effectedBlocks != null && !effectedBlocks.isEmpty())
        {
            FMLCommonHandler.instance().bus().register(this);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
        {
            Iterator<Placement> it = effectedBlocks.iterator();
            int c = 0;
            while(it.hasNext() && c++ <= blocksPerTick)
            {
                blast.handleBlockPlacement(it.next());
                it.remove();
            }
        }
        if(effectedBlocks.isEmpty())
        {
            FMLCommonHandler.instance().bus().unregister(this);
        }
    }
}