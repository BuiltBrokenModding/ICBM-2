package icbm.api.sentry;

public class AmmoPair<IAmmo, ItemStack>
{
	IAmmo ammo;
	ItemStack itemStack;

	public AmmoPair(IAmmo ammo, ItemStack itemStack)
	{
		this.ammo = ammo;
		this.itemStack = itemStack;
	}

	public IAmmo getAmmo()
	{
		return ammo;
	}

	public ItemStack getStack()
	{
		return itemStack;
	}

}
