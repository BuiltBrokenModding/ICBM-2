package icbm.core.di;

import icbm.core.ICBMTab;
import icbm.core.ZhuYao;
import net.minecraft.item.Item;

public class ItICBM extends Item
{
	public ItICBM(int id, String name)
	{
		super(ZhuYao.CONFIGURATION.getItem(name, id).getInt());
		this.setUnlocalizedName(ZhuYao.PREFIX + name);
		this.setCreativeTab(ICBMTab.INSTANCE);
	}
}
