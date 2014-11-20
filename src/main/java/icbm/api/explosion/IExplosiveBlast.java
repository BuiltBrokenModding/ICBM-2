package icbm.api.explosion;

import net.minecraft.world.World;
import resonant.lib.transform.vector.Vector3;

import java.util.Collection;

/** Used to do the actions caused by the IExplosive instance. Make sure to construct the
 * blast with the location info needed.
 * If your blast is threaded avoid using synchronized as this could lock up the thread.
 *
 * Created on 11/18/2014.
 * @author Darkgurdsman
 */
public interface IExplosiveBlast
{
    /**
     * Should the result of the blast be threaded to reduce impact on server performance. Only
     * thread an explosive if its rather larger as creating threads for a single small explosives
     * is wasteful.
     * @param triggerCause
     * @return true if it should be threaded, false if the explosion doesn't effect blocks or is very small
     */
    public boolean shouldThreadExplosion(TriggerCause triggerCause);

    /**
     * Gets all blocks effected by the blast, during this call never edit anything in the world. Instead
     * just do basic logic on if the block should be removed, or effected. If the block is effected add it
     * to a list then return the list. This way if the blast is threaded, or a test run the locations can
     * be feed back into the doEffect method.
     * @param triggerCause - cause of the blast
     * @return
     */
    public Collection<Vector3> getEffectedBlocks(TriggerCause triggerCause);

    /** Called to actually effect blocks from the list return by getEffectedBlocks.
     * @param blocks - list of blocks to edit
     * @param triggerCause - cause of the blast
     */
    public void doEffectBlocks(Collection<Vector3> blocks, TriggerCause triggerCause);

    /**
     * Called to effect other things than blocks like entities. This is normally called right after
     * the first blocks are removed by the blast thread.
     * @param world - world to do actions in
     * @param x
     * @param y
     * @param z
     * @param triggerCause - cause of the blast
     */
    public void doEffectOther(World world, double x, double y, double z, TriggerCause triggerCause);
}
