package com.builtbroken.icbm.content.crafting.missile.guidance;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.ItemAbstractModule;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Item for the modules to be contained in an inventory
 * Created by robert on 12/28/2014.
 */
public class ItemGuidanceModules extends ItemAbstractModule
{
    public ItemGuidanceModules()
    {
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (GuidanceModules engine : GuidanceModules.values())
        {
            ItemStack stack = engine.newModuleStack();
            if (stack != null)
                list.add(stack);
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        return GuidanceModules.get(meta) != null ? GuidanceModules.get(meta).icon : this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(ICBM.PREFIX + "engine");
        for (GuidanceModules engine : GuidanceModules.values())
        {
            engine.icon = reg.registerIcon(ICBM.PREFIX + engine.name);
        }
    }
}
