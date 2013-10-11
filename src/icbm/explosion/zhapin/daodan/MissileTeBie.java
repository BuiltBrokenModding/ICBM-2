package icbm.explosion.zhapin.daodan;

import icbm.explosion.ICBMExplosion;
import net.minecraft.item.ItemStack;

public abstract class MissileTeBie extends Missile
{
	public MissileTeBie(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
		this.hasGrenade = false;
		this.hasMinecart = false;
	}

	@Override
	public ItemStack getItemStack()
	{
		return new ItemStack(ICBMExplosion.itemMissile, 1, this.getID());
	}
}
