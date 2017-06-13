package com.builtbroken.icbm.content.items.parts;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Item for crafting parts enum, used to craft missiles
 * Created by Dark on 8/25/2015.
 */
public class ItemMissileParts extends Item
{
    public ItemMissileParts()
    {
        this.setHasSubtypes(true);
        this.setUnlocalizedName(ICBM.PREFIX + "missileParts");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < MissileCraftingParts.values().length)
            return super.getUnlocalizedName() + "." + MissileCraftingParts.values()[stack.getItemDamage()].name;
        return super.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (MissileCraftingParts sheet : MissileCraftingParts.values())
        {
            list.add(sheet.stack());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (MissileCraftingParts sheet : MissileCraftingParts.values())
        {
            sheet.icon = reg.registerIcon(ICBM.PREFIX + sheet.name);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta >= 0 && meta < MissileCraftingParts.values().length)
        {
            return MissileCraftingParts.values()[meta].icon;
        }
        return ItemSheetMetal.SheetMetal.FULL.icon;
    }
}
