package resonant.lib.world;

import java.util.Collection;

/** Handler for any action that change the world.
 *
 * If you implement this make sure you populate your object with the world, x, y, z
 * and trigger cause if you need that information. Nothing else will be provide for
 * you when creating these actions.
 *
 * Created on 11/18/2014.t
 * @author Darkgurdsman
 */
public interface IWorldChangeAction
{
    /**
     * Should the result of the blast be threaded to reduce impact on server performance. Only
     * thread an explosive if its rather larger as creating threads for a single small explosives
     * is wasteful.
     * @return number of blocks to change per tick, zero and bellow are treated as not mutli-threaded
     */
    public int shouldThreadAction();

    /**
     * Gets all blocks effected by the blast, during this call never edit anything in the world. Instead
     * just do basic logic on if the block should be removed, or effected. If the block is effected add it
     * to a list then return the list. This way if the blast is threaded, or a test run the locations can
     * be feed back into the doEffect method.
     * @return list of vectors containing the block to set at that location
     */
    public Collection<BlockEdit> getEffectedBlocks();

    /** Called to actually effect blocks from the list return by getEffectedBlocks.
     * @param blocks - block to change
     */
    public void handleBlockPlacement(BlockEdit blocks);

    /** Called to effect other things than blocks like entities.
     * Called before and after blocks have been placed into the world.
     * If your action is threaded then the after method call may be several ticks
     * after the first call.
     * @param beforeBlocksPlaced - if true this means no blocks have been placed, false all blocks have been placed
     */
    public void doEffectOther(boolean beforeBlocksPlaced);
}
