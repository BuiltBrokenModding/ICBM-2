package icbm.api.sentry;

import net.minecraft.item.ItemStack;

public interface IAmmunition
{

    /** Called when an ammo container goes to drop this item into the world. Useful to prevent
     * infinite ammo from being dropped */
    public boolean canDrop(int meta);

    /** Called when the item is added to the world when a ammo container is forcefully broken. Useful
     * it the ammo should detonate or cause extra havoc
     * 
     * @return what is left of the stack after action. Return entire stack if nothing happens */
    public ItemStack onDroppedIntoWorld(ItemStack itemStack);

    public ProjectileType getType(ItemStack itemStack);

    public int getDamage();

}
