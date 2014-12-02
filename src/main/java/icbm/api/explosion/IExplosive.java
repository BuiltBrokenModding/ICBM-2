package icbm.api.explosion;

import net.minecraft.world.World;
import resonant.lib.world.IWorldChangeAction;

/**
 * Applied to any explosive handler that created a blast. Should only create
 * one explosive instance per subtype of blast to avoid creating extra ids.
 * Use trigger to express how the blast will be created rather than creating
 * a new explosive instance per different blast.
 */
public interface IExplosive
{

	/** Attempt to trigger the explosive at the location for the trigger cause.
     * Most of the time this will be call once so avoid chance logic as it maybe not
     * get called again.
	 *
	 * @param world  The world in which the explosion takes place.
	 * @param x      The X-Coord
	 * @param y      The Y-Coord
	 * @param z      The Z-Coord
	 * @param triggerCause - object that describes what caused the explosion to try
     * @param yieldMultiplier - multiplier the size of the explosive
     * @return instanceof IWorldChangeAction that tells what blocks and entities are to be effected
	 */
	public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, int yieldMultiplier);

    /**
     * Called when the explosive is registered
     */
    public void onRegistered();

    /**
     * @return The unique name key in the ICBM language file.
     */
    public String getUnlocalizedName();

}
