package icbm.explosion;

import java.util.Collection;
import java.util.HashMap;

import icbm.Reference;
import resonant.lib.world.IWorldChangeAction;
import net.minecraft.world.World;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.TriggerCause;
import resonant.lib.transform.vector.VectorWorld;
import resonant.lib.world.WorldChangeHelper;

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
            Reference.LOGGER.info("ExplosiveRegistry> Mod: " + modID + "  Registered explosive instance " + ex);
            return ex;
        }
        else
        {
            return get(ex.getUnlocalizedName());
        }
    }

    /** Called to trigger an explosion at the location
     *
     * @param ex - explosive handler, used to create the IWorldChangeAction instance
     * @param triggerCause - cause of the trigger
     * @param multi - size of the action
     * @return if the result completed, was blocked, or failed
     */
    public static WorldChangeHelper.ChangeResult triggerExplosive(World world, double x, double y, double z, IExplosive ex, TriggerCause triggerCause, int multi)
    {
        return triggerExplosive(new VectorWorld(world, x, y, z), ex, triggerCause, multi);
    }

    /** Called to trigger an explosion at the location
     *
     * @param loc - location in the world
     * @param ex - explosive handler, used to create the IWorldChangeAction instance
     * @param triggerCause - cause of the trigger
     * @param multi - size of the action
     * @return if the result completed, was blocked, or failed
     */
    public static WorldChangeHelper.ChangeResult triggerExplosive(VectorWorld loc, IExplosive ex, TriggerCause triggerCause, int multi)
    {
        if(isRegistered(ex))
        {
            IWorldChangeAction blast = ex.createBlastForTrigger(loc.world(), loc.x(), loc.y(), loc.z(), triggerCause, multi);
            return WorldChangeHelper.doAction(loc, blast, triggerCause);
        }
        return WorldChangeHelper.ChangeResult.FAILED;
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


}
