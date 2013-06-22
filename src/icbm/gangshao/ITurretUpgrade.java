package icbm.gangshao;

import icbm.gangshao.turret.upgrades.ItPaoTaiUpgrades.TurretUpgradeType;
import net.minecraft.item.ItemStack;

/**
 * Applied to classes that extends Item.class and are designed to upgrade sentries or turrets
 */
public interface ITurretUpgrade
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
	public TurretUpgradeType getType(ItemStack itemstack);
}
