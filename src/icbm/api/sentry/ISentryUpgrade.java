package icbm.api.sentry;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * Applied to classes that extends Item.class and are designed to upgrade sentries or turrets
 */
public interface ISentryUpgrade
{
	/**
	 * Gets the type of the object using a string name *
	 * 
	 * Suggested types that will be implemented by most sentries HeatSink - increases the turrets
	 * max heat storage TargetRange - increases the turrets max targeting range TargetSpeed -
	 * increase the turrets max rotation speed when it has a target FiringRate - increase the
	 * turrets max firing rate
	 * 
	 * @return Empty list or list containing string types
	 */
	public List<String> getTypes(ItemStack itemstack);

	/**
	 * How effective is this item (100% = 1.0) As in how much percent does this increase the current
	 * value. Each item loses 10% when stacked with each other
	 */
	public float getEffectiveness(ItemStack itemstack);
}
