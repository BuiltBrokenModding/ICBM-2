package com.builtbroken.icbm.content.launcher.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.jlib.lang.TextColor;
import com.builtbroken.mc.api.items.IPassCodeItem;
import com.builtbroken.mc.api.items.IWorldPosItem;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.prefab.items.ItemWorldPos;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by robert on 4/15/2015.
 */
public class ItemLinkTool extends ItemWorldPos implements IWorldPosItem, IPassCodeItem, IPostInit
{
    IIcon linked_icon;

    public ItemLinkTool()
    {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.itemLinkTool), " I ", "BCB", "ICI", 'I', Items.iron_ingot, 'B', Blocks.wooden_button, 'C', UniversalRecipe.CIRCUIT_T2.get()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(ICBM.PREFIX + "linker.unlinked");
        this.linked_icon = reg.registerIcon(ICBM.PREFIX + "linker.linked");
    }

    @Override
    public IIcon getIconFromDamage(int meta)
    {
        if (meta == 1)
            return this.linked_icon;
        return this.itemIcon;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (player.isSneaking())
        {
            stack.setTagCompound(null);
            stack.setItemDamage(0);
            LanguageUtility.addChatToPlayer(player, "link.cleared");
            player.inventoryContainer.detectAndSendChanges();
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hit_x, float hit_y, float hit_z)
    {
        if (world.isRemote)
            return true;

        Location location = new Location(world, x, y, z);
        TileEntity tile = location.getTileEntity();

        if (player.isSneaking())
        {
            setLocation(stack, location);
            LanguageUtility.addChatToPlayer(player, "link.pos.set");
            if (tile instanceof IPassCode)
            {
                setCode(stack, ((IPassCode) tile).getCode());
            }
            stack.setItemDamage(1);
            player.inventoryContainer.detectAndSendChanges();
            return true;
        }
        else
        {
            Location storedLocation = getLocation(stack);
            if (storedLocation == null || !storedLocation.isAboveBedrock())
            {
                LanguageUtility.addChatToPlayer(player, "link.error.pos.invalid");
                return true;
            }
            else if (tile instanceof ILinkable)
            {
                String result = ((ILinkable) tile).link(getLocation(stack), getCode(stack));
                if (result != null && result != "")
                {
                    if (result.contains("error"))
                    {
                        String translation = LanguageUtility.getLocalName(result);
                        if (translation == null || translation.isEmpty())
                            translation = "Error";
                        player.addChatComponentMessage(new ChatComponentText(TextColor.RED.getColorString() + translation));
                    }
                    else
                    {
                        LanguageUtility.addChatToPlayer(player, result);
                    }
                }
                else
                {
                    LanguageUtility.addChatToPlayer(player, "link.completed");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public short getCode(ItemStack stack)
    {
        if (stack.getItem() == this && stack.hasTagCompound() && stack.getTagCompound().hasKey("passShort"))
        {
            return stack.getTagCompound().getShort("passShort");
        }
        return 0;
    }

    @Override
    public void setCode(ItemStack stack, short code)
    {
        if (stack.getItem() == this)
        {
            if (!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setShort("passShort", code);
        }
    }
}
