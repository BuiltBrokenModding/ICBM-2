package icbm.sentry.interfaces;

import java.util.List;

import net.minecraft.item.ItemStack;

/** Applied to classes that extends Item.class and are designed to upgrade sentries or turrets */
public interface ITurretUpgrade
{
    /* Upgrade names supported by the sentries */
    public static final String SHELL_COLLECTOR = "shell_collector";
    public static final String TARGET_RANGE = "target_range";

    /** Gets the type of the object using a string name.
     * 
     * @return Empty list or list containing string types */
    public void getTypes(List<String> types, ItemStack itemStack);

    /** Gets how effective the upgrade is on a scale of neg 1.0 - pos 1.0. Zero having no effect, pos
     * having a good effect, neg having a bad effect. Types that have no percentage effect will
     * ignore this method. For example shell collectors either work or don't work. */
    public double getUpgradeEfficiance(ItemStack itemStack, String type);
}
