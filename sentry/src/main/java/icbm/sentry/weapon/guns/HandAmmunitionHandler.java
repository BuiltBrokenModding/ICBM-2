package icbm.sentry.weapon.guns;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import resonant.api.weapon.IAmmunition;

/** Static class for universal ammunition handling.
 * 
 * @author Archtikz */
public class HandAmmunitionHandler
{

    public static boolean isEmpty(EntityPlayer player, ItemStack weaponStack)
    {
        if (weaponStack.stackTagCompound != null && weaponStack.stackTagCompound.hasKey("clipCurrentAmmo"))
            return weaponStack.stackTagCompound.getInteger("clipCurrentAmmo") == 0;
        else if (player.capabilities.isCreativeMode)
            return false;
        else
            return true;
    }

    public static void reload(ItemStack weaponStack)
    {
        if (weaponStack.stackTagCompound.hasKey("clipCapacity"))
            weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", weaponStack.stackTagCompound.getInteger("clipCapacity"));
    }

    public static int getCurrentAmmo(ItemStack weaponStack)
    {
        if (weaponStack.stackTagCompound != null && weaponStack.stackTagCompound.hasKey("clipCurrentAmmo"))
            return weaponStack.stackTagCompound.getInteger("clipCurrentAmmo");
        else
            return 0;
    }

    public static void consume(ItemStack weaponStack, int amt)
    {
        if (amt < 0)
        {
            return;
        }
        if (weaponStack.stackTagCompound.hasKey("clipCapacity"))
        {
            if (amt > weaponStack.stackTagCompound.getInteger("clipCapacity"))
            {
                weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", 0);
                return;
            }
        }
        if (weaponStack.stackTagCompound.hasKey("clipCurrentAmmo"))
            weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", weaponStack.stackTagCompound.getInteger("clipCurrentAmmo") - amt);
    }

    public static ItemStack searchInventoryForAmmo(EntityPlayer player, boolean reality)
    {
        for (int i = 0; i < player.inventory.mainInventory.length; i++)
        {
            if (player.inventory.mainInventory[i] != null)
            {
                if (player.inventory.mainInventory[i].getItem() instanceof IAmmunition)
                {
                    ItemStack stack = player.inventory.mainInventory[i];
                    if (reality)
                    {
                        if (!player.capabilities.isCreativeMode)
                            player.inventory.mainInventory[i] = null;
                    }
                    return stack;
                }
            }
        }
        return null;
    }
}
