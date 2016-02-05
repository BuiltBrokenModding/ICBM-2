package com.builtbroken.icbm.content.crafting.missile.guidance;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.content.crafting.missile.ItemAbstractModule;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Item for the modules to be contained in an inventory
 * Created by robert on 12/28/2014.
 */
public class ItemGuidanceModules extends ItemAbstractModule implements IPostInit
{
    public ItemGuidanceModules()
    {
        this.setHasSubtypes(true);
    }

    @Override
    public void onPostInit()
    {
        for (GuidanceModules guidance : GuidanceModules.values())
        {
            IGuidance e = guidance.newModule();
            if (e instanceof IPostInit)
            {
                ((IPostInit) e).onPostInit();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        IModule module = getModule(stack);
        if (module instanceof IGuidance)
        {
            list.add("FallOff: " + ((IGuidance) module).getFallOffRange(null));
            list.add("FailureRate: " + ((IGuidance) module).getChanceToFail(null));
        }
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
