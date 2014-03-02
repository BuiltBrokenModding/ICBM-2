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

    /** Called when the ammo is consumed, modify the stack to show it has been consumed, null if
     * fully consumed */
    public ItemStack onConsumed(ItemStack itemStack);

    /** Gets the item stack that is the shell for the ammo */
    public ItemStack getShell(ItemStack itemStack);

    /** Type of project only used to restrict ammo use */
    public ProjectileType getType(ItemStack itemStack);

    /** Gets the damage this ammo item should do when fired */
    public float getDamage();

}
