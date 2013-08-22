package icbm.zhapin.zhapin.daodan;

import icbm.zhapin.ZhuYaoZhaPin;
import net.minecraft.item.ItemStack;

public abstract class DaoDanTeBie extends DaoDan
{
	public DaoDanTeBie(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
		this.hasGrenade = false;
		this.hasMinecart = false;
	}

	@Override
	public ItemStack getItemStack()
	{
		return new ItemStack(ZhuYaoZhaPin.itDaoDan, 1, this.getID());
	}
}
