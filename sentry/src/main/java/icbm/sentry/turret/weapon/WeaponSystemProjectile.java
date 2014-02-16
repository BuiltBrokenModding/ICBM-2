package icbm.sentry.turret.weapon;

import icbm.api.sentry.IAmmunition;
import icbm.sentry.turret.Sentry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class WeaponSystemProjectile extends WeaponSystem
{
    public WeaponSystemProjectile(Sentry sentry)
    {
        super(sentry);
    }

    public boolean isAmmo(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof IAmmunition;
    }

    /** Used to consume ammo or check if ammo can be consumed
     * 
     * @param count - number of items to consume
     * @param doConsume - true items will be consumed
     * @return true if all rounds were consumed */
    public boolean consumeAmmo(int count, boolean doConsume)
    {
        if (count > 0 && sentry.getHost() instanceof IInventory)
        {
            //TODO add a way to restrict this to a set range of slots
            IInventory inv = ((IInventory) sentry.host);
            //0-4 are upgrade slots for the sentry, 5-8 are ammo slots
            int consumeCount = 0;
            for (int slot = 5; slot < inv.getSizeInventory(); slot++)
            {
                ItemStack stack = inv.getStackInSlot(slot);
                if (isAmmo(stack))
                {
                    if (stack.stackSize >= count)
                    {
                        if (doConsume)
                            stack.stackSize -= count;
                        return true;
                    }
                    else
                    {

                    }
                }
            }
        }
        return false;
    }
}
