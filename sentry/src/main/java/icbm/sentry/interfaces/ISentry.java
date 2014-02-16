package icbm.sentry.interfaces;

import calclavia.lib.utility.nbt.ISaveObj;

/** Interface applied to all sentry objects. Any sentry that uses this must have a constructor that contains ISentryContainer parameter 
 * 
 * @author DarkGaurdsman */
public interface ISentry extends ISaveObj
{
    /** NBT string id that all sentries should use to identify themselves threw a NBTTagCompound save */
    public static final String SENTRY_SAVE_ID = "sentryID";

    /** NBT string id that all sentries should save too in a TileEntity, ItemStack, or Entity. This
     * should contain the sentry id and other data used to recreate the sentry from a save */
    public static final String SENTRY_OBJECT_SAVE = "sentryTile";
    
    public ISentryContainer getHost();
}
