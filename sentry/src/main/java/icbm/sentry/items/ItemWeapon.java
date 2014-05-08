package icbm.sentry.items;

import icbm.sentry.interfaces.IWeaponSystem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import calclavia.api.icbm.sentry.IAmmunition;
import calclavia.lib.prefab.item.ItemTooltip;

/** Prefab for all weapon to be based on in ICBM. Does the same basic logic as sentry guns to fire
 * the weapon.
 * 
 * @author Darkguardsman */
public abstract class ItemWeapon extends ItemTooltip implements IItemWeaponProvider
{
    public ItemWeapon(int id)
    {
        super(id);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        player.addChatMessage("Click");
        IWeaponSystem weapon = this.getWeaponSystem(itemstack, player);
        
        if (canFireWeapon(weapon, itemstack, world, player, 1))
        {
            ItemStack ammoStack = consumeAmmo(weapon, itemstack, world, player, 1, false);
            if (true || ammoStack != null)
            {
                int roundsFired = ammoStack != null ? ammoStack.getItem() instanceof IAmmunition ? ((IAmmunition) ammoStack.getItem()).getAmmoCount(ammoStack) : ammoStack.stackSize : 0;
                if (true || roundsFired >= 1)
                {
                    onWeaponFired(weapon, itemstack, ammoStack, world, player);
                }
            }
        }
        return itemstack;
    }

    /** Basic check if the weapon can be fired.
     * 
     * @param itemstack - weapon
     * @param world - world the weapon is going to be firing in
     * @param player - player firing the weapon
     * @param rounds - rounds the will be consumed when fired
     * @return true if the weapon can be fired */
    public boolean canFireWeapon(IWeaponSystem weapon, ItemStack itemstack, World world, EntityPlayer player, int rounds)
    {
        return true;
    }

    /** Called when the player fires the weapon, should handle all weapon firing actions, audio, and
     * effects. Shouldn't handle ammo.
     * 
     * @param itemstack
     * @param world
     * @param player */
    public void onWeaponFired(IWeaponSystem weapon, ItemStack weaponStack, ItemStack ammoStack, World world, EntityPlayer player)
    {
        player.addChatMessage("Weapon Fired");
        weapon.fire(150);
    }

    /** Called to consume ammo or check if ammo can be consumed.
     * 
     * @param itemstack - weapon consuming the ammo
     * @param world - world the ammo is consumed in
     * @param player - player consuming the ammo
     * @param rounds - rounds to consume from the ammo stack
     * @param doConsume - true will consume the ammo from the gun or player's inventory
     * @return Ammo stack that was consumed. Make sure to do an additional stack size or ammo count
     * check */
    public ItemStack consumeAmmo(IWeaponSystem weapon, ItemStack itemstack, World world, EntityPlayer player, int rounds, boolean doConsume)
    {
        player.addChatMessage("Ammo Consumed");
        return null;
    }

}
