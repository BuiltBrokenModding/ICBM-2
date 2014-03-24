package icbm.sentry.interfaces;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;
import calclavia.api.IPos;
import calclavia.lib.utility.nbt.ISaveObj;

/** *WIP* Interface applied to all sentry objects. Any sentry that uses this must have a constructor
 * that contains ISentryContainer parameter
 * 
 * @author DarkGaurdsman */
public interface ITurret extends ISaveObj, IPos
{
    /** NBT string id that all sentries should use to identify themselves threw a NBTTagCompound save */
    public static final String SENTRY_TYPE_SAVE_ID = "sentryID";

    /** NBT string id that all sentries should save too in a TileEntity, ItemStack, or Entity. This
     * should contain the sentry id and other data used to recreate the sentry from a save */
    public static final String SENTRY_OBJECT_SAVE = "sentryTile";

    //Sentry traits constants *WIP*
    public static final String SEARCH_RANGE_TRAIT = "search.range";

    /** called each tick by the host to update the sentry */
    public void update();

    /** Gets the Tile or Entity that is hosting the sentry */
    public ITurretProvider getHost();

    /** Offset from the center offset to were the end of the barrel should be at */
    public Vector3 getAimOffset();

    /** Triggers the sentry to fire its weapon system at a location
     * 
     * @return if the weapon activated without error
     * @param target - Vector3 location to fire the weapon at */
    public boolean fire(Vector3 target);

    /** Triggers the sentry to fire its weapon system at an entity. If the sentry fires like a gun
     * just pass this into fire(Vector3) and use a raytrace. This way the sentry operate more
     * realistic, and doesn't auto damage the target.
     * 
     * @return if the weapon activated without error
     * @param target - Entity to fire the weapon at */
    public boolean fire(Entity target);

    /** Max targeting range of the sentry */
    public double getRange();

    /** Map of upgrades and how much effect they have on the sentry */
    public HashMap<String, Double> upgrades();

    /** Map of main traits applied to the sentry */
    public HashMap<String, Double> traits();
}
