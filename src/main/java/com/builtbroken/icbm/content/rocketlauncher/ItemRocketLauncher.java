package com.builtbroken.icbm.content.rocketlauncher;

import com.builtbroken.icbm.api.IAmmoType;
import com.builtbroken.icbm.api.IWeapon;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import com.builtbroken.icbm.ICBM;
import resonant.api.explosive.IExplosive;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import resonant.lib.utility.LanguageUtility;

import java.util.HashMap;
import java.util.List;

/**
 * Rocket Launcher
 *
 * @author Calclavia
 */

public class ItemRocketLauncher extends Item implements IWeapon
{
    private static final int firingDelay = 1000;
    private HashMap<String, Long> clickTimePlayer = new HashMap<String, Long>();

    public ItemRocketLauncher()
    {
        super();
        this.setUnlocalizedName("rocketLauncher");
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            long clickMs = System.currentTimeMillis();
            if (clickTimePlayer.containsKey(player.getDisplayName()))
            {
                if (clickMs - clickTimePlayer.get(player.getDisplayName()) < firingDelay)
                {
                    //TODO play weapon empty click audio to note the gun is reloading
                    return itemStack;
                }
            }


            // Check the player's inventory and look for missiles.
            for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
            {
                ItemStack inventoryStack = player.inventory.getStackInSlot(slot);

                if (inventoryStack != null)
                {
                    if (inventoryStack.getItem() instanceof ItemMissile)
                    {
                        IExplosive ex = ((ItemMissile) inventoryStack.getItem()).getExplosive(inventoryStack);

                        if (ex != null)
                        {
                            EntityMissile.fireMissileByEntity(player, inventoryStack);

                            if (!player.capabilities.isCreativeMode)
                            {
                                player.inventory.setInventorySlotContents(slot, null);
                            }

                            //Store last time player launched a rocket
                            clickTimePlayer.put(player.getDisplayName(), clickMs);

                            return itemStack;
                        }
                    }
                }
            }
        }

        return itemStack;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4)
    {
        String str = LanguageUtility.getLocal("info.icbm:rocketlauncher.tooltip");
        list.add(str);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        ItemStack currentItem = event.player.getCurrentEquippedItem();

        if (currentItem != null && (event.player != Minecraft.getMinecraft().renderViewEntity || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0))
        {
            if (currentItem.getItem() == ICBM.itemRocketLauncher)
            {
                if (event.player.getItemInUseCount() <= 0)
                {
                    event.player.setItemInUse(currentItem, Integer.MAX_VALUE);
                }
            }
        }
    }

    @Override
    public ItemStack loadAmmo(ItemStack weapon, ItemStack ammo, IAmmoType type, boolean isClip)
    {
        return null;
    }

    @Override
    public boolean canContainAmmo(ItemStack weapon)
    {
        return false;
    }
}
