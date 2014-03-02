package icbm.api.sentry;

import net.minecraft.item.ItemStack;

public interface IAmmunition
{
    /** Called when the item is added to the world when a ammo container is forcefully broken. Useful
     * it the ammo should detonate or cause extra havoc
     * 
     * @return what is left of the stack after action. Return entire stack if nothing happens */
    public ItemStack onDroppedIntoWorld(ItemStack itemStack);

    /** Called when the ammo is consumed, modify the stack to show it has been consumed, null if
     * fully consumed */
    public ItemStack consumeAmmo(ItemStack itemStack, int count);

    /** Amount of rounds of ammo this itemStack counts towards */
    public int getAmmoCount(ItemStack itemStack);

    /** Gets the item stack that is the shell for the ammo */
    public ItemStack getShell(ItemStack itemStack, int count);

    /** Type of project only used to restrict ammo use */
    public ProjectileType getType(ItemStack itemStack);

    /** Gets the damage this ammo item should do when fired */
    public float getDamage();

}
