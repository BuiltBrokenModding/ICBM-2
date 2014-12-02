package icbm.explosion.thread;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import icbm.api.event.ExplosiveEvent;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveBlast;
import icbm.api.explosion.TriggerCause;
import io.netty.util.internal.ConcurrentSet;
import net.minecraftforge.common.MinecraftForge;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;

import java.util.Collection;
import java.util.Iterator;

public class ThreadExplosion extends Thread
{
    public static ConcurrentSet<ThreadExplosion> set = new ConcurrentSet<>();
    public final VectorWorld position;
    public final IExplosiveBlast blast;
    public final IExplosive ex;
    public final TriggerCause triggerCause;

    public int blocksPerTick = 20;

    private Collection<Vector3> effectedBlocks;

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
        effectedBlocks = blast.getEffectedBlocks(triggerCause);
        Event event = new ExplosiveEvent.BlocksEffectedExplosiveEvent(position, ex, triggerCause, blast, effectedBlocks);
        MinecraftForge.EVENT_BUS.post(event);
        if(effectedBlocks != null && !effectedBlocks.isEmpty())
        {
            FMLCommonHandler.instance().bus().register(this);
            set.add(this);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
        {
            Iterator<Vector3> it = effectedBlocks.iterator();
            int c = 0;
            while(it.hasNext() && c++ <= blocksPerTick)
            {
                blast.doEffectBlock(it.next(), triggerCause);
                it.remove();
            }
        }
        if(effectedBlocks.isEmpty())
        {
            FMLCommonHandler.instance().bus().unregister(this);
            set.remove(this);
        }
    }
}