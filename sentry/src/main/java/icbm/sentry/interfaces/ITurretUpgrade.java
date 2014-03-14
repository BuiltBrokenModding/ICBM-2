package icbm.sentry.interfaces;

import net.minecraft.item.ItemStack;

/** Applied to classes that extends Item.class and are designed to upgrade sentries or turrets */
public interface ITurretUpgrade
{
    /* Upgrade names supported by the sentries */
    public static final String SHELL_COLLECTOR = "shell_collector";
    public static final String TARGET_RANGE = "target_range";

    /** Gets the type of the object using a string name
     * 
     * @return Empty list or list containing string types */
    public String getType(ItemStack itemStack);

    /** Damages the item when used in the sentry. Its optional to damage the item however if you do
     * its suggest to use NBT.
     * 
     * @param item - itemStack
     * @param damage
     * @return True if the item is destroyed */
    public boolean damageUpgrade(ItemStack item, int damage);

    /** Check to see if the upgrade still works. Used in the case that the item was damaged and can
     * no longer work. */
    public boolean isFunctional(ItemStack item);
}
