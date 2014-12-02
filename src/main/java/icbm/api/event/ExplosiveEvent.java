package icbm.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveBlast;
import icbm.api.explosion.TriggerCause;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;

import java.util.Collection;

/**
 * Created by robert on 11/18/2014.
 */
public class ExplosiveEvent extends WorldEvent
{
    public final Vector3 vec;
    public final IExplosive ex;

    public ExplosiveEvent(World world, Vector3 vec, IExplosive ex)
    {
        super(world);
        this.vec = vec;
        this.ex = ex;
    }

    /**
     * Called before a blast instance is created. Used to cancel the blast before its even created
     */
    @Cancelable
    public static class OnExplosiveTriggeredEvent extends ExplosiveEvent
    {
        public final TriggerCause triggerCause;

        public OnExplosiveTriggeredEvent(World world, Vector3 vec, IExplosive ex, TriggerCause triggerCause)
        {
            super(world, vec, ex);
            this.triggerCause = triggerCause;
        }
    }

    /**
     * Called after the blast is created but before anything is calculated
     */
    @Cancelable
    public static class OnBlastCreatedEvent extends ExplosiveEvent
    {
        public final TriggerCause triggerCause;
        public final IExplosiveBlast blast;

        public OnBlastCreatedEvent(World world, Vector3 vec, IExplosive ex, TriggerCause triggerCause, IExplosiveBlast blast)
        {
            super(world, vec, ex);
            this.triggerCause = triggerCause;
            this.blast = blast;
        }
    }

    /**
     * Make sure your usage of the event is thread safe as this may be called
     * inside the world or outside of the world thread
     *
     * Called after the blast has calculated its effect area. Populates a list
     * of vector3 that can be modified.
     */
    public static class BlocksEffectedExplosiveEvent extends ExplosiveEvent
    {
        public final TriggerCause triggerCause;
        public final IExplosiveBlast blast;
        public final Collection<Vector3> list;

        public BlocksEffectedExplosiveEvent(VectorWorld vec, IExplosive ex, TriggerCause triggerCause, IExplosiveBlast blast, Collection<Vector3> list)
        {
            this(vec.world(), vec, ex, triggerCause, blast, list);
        }

        public BlocksEffectedExplosiveEvent(World world, Vector3 vec, IExplosive ex, TriggerCause triggerCause, IExplosiveBlast blast, Collection<Vector3> list)
        {
            super(world, vec, ex);
            this.triggerCause = triggerCause;
            this.blast = blast;
            this.list = list;
        }
    }
}
