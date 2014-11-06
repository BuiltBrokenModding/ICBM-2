package icbm.sentry.interfaces;

import java.util.List;

import net.minecraft.item.ItemStack;

/** Applied to classes that extends Item.class and are designed to upgrade a device
 * 
 * @author DarkGuardsman */
public interface IUpgrade
{
    /* Upgrade names supported by the sentries */
    public static final String SHELL_COLLECTOR = "shellCollector";
    public static final String SILENCER = "silencer";
    public static final String TARGET_RANGE = "target.range";
    public static final String TARGET_SPEED = "target.speed";
    public static final String ENERGY_STORAGE = "energy.storage";
    public static final String ENERGY_EFFICIENCY = "energy.efficiency";

    /** Gets the type of the object using a string name.
     * 
     * @return Empty list or list containing string types */
    public void getTypes(List<String> types, ItemStack itemStack);

    /** Gets how effective the upgrade is on a scale of neg 1.0 - pos 1.0. Zero having no effect, pos
     * having a good effect, neg having a bad effect. Types that have no percentage effect will
     * ignore this method. For example shell collectors either work or don't work. */
    public double getUpgradeEfficiance(ItemStack itemStack, String type);

    /** Checks to see if the upgrade can be applied to the object.
     * 
     * @param object - TileEntity, Item, Entity, ITurret, IMissile, etc anything
     * @return true if the upgrade can be applied */
    public boolean canApplyTo(ItemStack itemStack, Object object);
}
