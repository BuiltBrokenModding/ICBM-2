package universalelectricity.prefab;

import net.minecraft.inventory.IInventory;
import universalelectricity.core.implement.IItemElectric;

/**
 * This slot should be used by any container that needs the slot for an electric items only.
 * 
 * @author Calclavia
 * 
 */
@Deprecated
public class SlotElectricItem extends SlotSpecific
{
	public SlotElectricItem(IInventory par2IInventory, int par3, int par4, int par5)
	{
		super(par2IInventory, par3, par4, par5, IItemElectric.class);
	}
}
