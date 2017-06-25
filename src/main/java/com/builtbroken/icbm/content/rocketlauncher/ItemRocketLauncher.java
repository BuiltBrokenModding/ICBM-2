package com.builtbroken.icbm.content.rocketlauncher;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.items.weapons.IItemAmmo;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;
import java.util.List;

/**
 * Rocket Launcher
 *
 * @author Calclavia, DarkGuardsman
 */

public class ItemRocketLauncher extends Item implements IItemReloadableWeapon, IPostInit
{
    private static final int firingDelay = 1000;
    private HashMap<String, Long> clickTimePlayer = new HashMap<String, Long>();

    public ItemRocketLauncher()
    {
        super();
        this.setUnlocalizedName(ICBM.PREFIX + "rocketLauncher");
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM_API.itemRocketLauncher), "III", " FC", "III", 'I', OreNames.INGOT_IRON, 'F', OreNames.FLINT, 'C', UniversalRecipe.CIRCUIT_T1.get()));
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
                    if (inventoryStack.getItem() instanceof IItemAmmo.IItemAmmoFireHandler)
                    {
                        IItemAmmo.IItemAmmoFireHandler ammo = (IItemAmmo.IItemAmmoFireHandler) inventoryStack.getItem();
                        if (ammo.isAmmo(inventoryStack))
                        {
                            IAmmoType type = ammo.getAmmoData(inventoryStack).getAmmoType();
                            if ("missile".equalsIgnoreCase(type.getAmmoCategory()) && ("micro".equalsIgnoreCase(type.getAmmoType()) || "small".equalsIgnoreCase(type.getAmmoType()) || Engine.runningAsDev))
                            {
                                ammo.fireAmmo(this, itemStack, inventoryStack, player);

                                if (!player.capabilities.isCreativeMode)
                                {
                                    ammo.consumeAmmo(this, itemStack, inventoryStack, 1);
                                    if (inventoryStack.stackSize <= 0)
                                    {
                                        player.inventory.setInventorySlotContents(slot, null);
                                    }
                                    player.inventoryContainer.detectAndSendChanges();
                                }

                                //Store last time player launched a rocket
                                clickTimePlayer.put(player.getDisplayName(), clickMs);

                                return itemStack;
                            }
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
        String str = LanguageUtility.getLocal("info.icbm:rocketLauncher.tooltip");
        list.add(str);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        ItemStack currentItem = event.player.getCurrentEquippedItem();

        if (currentItem != null && (event.player != Minecraft.getMinecraft().renderViewEntity || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0))
        {
            if (currentItem.getItem() == ICBM_API.itemRocketLauncher)
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

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return Items.stone_shovel.getIcon(stack, pass);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister p_94581_1_)
    {
        //No icon to register
    }
}
