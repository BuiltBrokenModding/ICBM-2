package icbm.sentry.items.weapons;

import icbm.sentry.interfaces.IWeaponSystem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IItemWeaponProvider
{
    public IWeaponSystem getWeaponSystem(ItemStack itemStack, Entity entity);
}
