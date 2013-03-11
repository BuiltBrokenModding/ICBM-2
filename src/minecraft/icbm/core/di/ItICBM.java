package icbm.core.di;

import icbm.api.ICBM;
import icbm.api.ICBMTab;
import icbm.core.ZhuYao;
import net.minecraft.item.Item;

public class ItICBM extends Item
{
	public ItICBM(int id, String name)
	{
		super(ICBM.CONFIGURATION.getItem(name, id).getInt());
		this.setUnlocalizedName(ZhuYao.PREFIX + name);
		this.setCreativeTab(ICBMTab.INSTANCE);
	}
}
