package icbm.core.di;

import icbm.core.ICBMTab;
import icbm.core.ZhuYaoICBM;
import net.minecraft.item.Item;

public class ItICBM extends Item
{
	public ItICBM(int id, String name)
	{
		super(ZhuYaoICBM.CONFIGURATION.getItem(name, id).getInt());
		this.setUnlocalizedName(ZhuYaoICBM.PREFIX + name);
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.func_111206_d(ZhuYaoICBM.PREFIX + name);

	}
}
