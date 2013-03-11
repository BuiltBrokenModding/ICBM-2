package icbm.sentry;

import icbm.api.ICBM;
import icbm.api.ICBMTab;
import net.minecraft.item.Item;

public class ItemSentryBase extends Item
{
	public ItemSentryBase(int id, String name)
	{
		super(ICBM.CONFIGURATION.getItem(name, id).getInt());
		this.setUnlocalizedName(ICBMSentry.PREFIX + name);
		this.setCreativeTab(ICBMTab.INSTANCE);
	}
}
