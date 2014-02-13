package icbm.sentry;

import icbm.sentry.turret.items.ItemSentryUpgrade.TurretUpgradeType;
import net.minecraft.item.ItemStack;

/** Applied to classes that extends Item.class and are designed to upgrade sentries or turrets */
public interface ITurretUpgrade
{
    /** Gets the type of the object using a string name *
     * 
     * Suggested types that will be implemented by most sentries HeatSink - increases the turrets
     * max heat storage TargetRange - increases the turrets max targeting range TargetSpeed -
     * increase the turrets max rotation speed when it has a target FiringRate - increase the
     * turrets max firing rate
     * 
     * @return Empty list or list containing string types */
    public TurretUpgradeType getType(ItemStack itemStack);

    /** Damages the item when used in the sentry. Its optional to damage the item however if you do
     * its suggest to use NBT.
     * 
     * @param itemStack - itemStack
     * @param damage
     * @return True if the item is destroyed */
    public boolean damageUpgrade(ItemStack item, int damage);

    /** Check to see if the upgrade still works. Used in the case that the item was damaged and can
     * no longer work. */
    public boolean isFunctional(ItemStack item);
}
