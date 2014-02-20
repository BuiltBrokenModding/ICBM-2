package icbm.sentry.interfaces;

import net.minecraft.entity.Entity;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.utility.nbt.ISaveObj;
import universalelectricity.api.vector.VectorWorld;

/** Interface applied to all sentry objects. Any sentry that uses this must have a constructor that
 * contains ISentryContainer parameter
 * 
 * @author DarkGaurdsman */
public interface ISentry extends ISaveObj
{
    /** NBT string id that all sentries should use to identify themselves threw a NBTTagCompound save */
    public static final String SENTRY_SAVE_ID = "sentryID";

    /** NBT string id that all sentries should save too in a TileEntity, ItemStack, or Entity. This
     * should contain the sentry id and other data used to recreate the sentry from a save */
    public static final String SENTRY_OBJECT_SAVE = "sentryTile";

    /** Gets the object that is hosting the sentry. Should only return TileEntity or Entity. */
    public ISentryContainer getHost();

    /** When not mounted by an entity can the sentry operate on its own */
    public boolean automated();

    /** can the sentry be mounted by an entity */
    public boolean mountable();

    /** Offset from the center offset to were the end of the barrel should be at */
    public Vector3 getAimOffset();

    /** Offset from host location to were the sentries center is located */
    public Vector3 getCenterOffset();

    /** Triggers the sentry to fire its weapon system at a location
     * 
     * @return if the weapon activated without error
     * @param target - Vector3 location to fire the weapon at */
    public boolean fire(Vector3 target);

    /** Triggers the sentry to fire its weapon system at an entity
     * 
     * @return if the weapon activated without error
     * @param target - Entity to fire the weapon at */
    public boolean fire(Entity target);

    public int getRange();
}
