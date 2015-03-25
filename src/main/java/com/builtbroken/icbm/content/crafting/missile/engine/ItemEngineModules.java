package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IModuleItem;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
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
public class ItemEngineModules extends Item implements IModuleItem
{
    public ItemEngineModules()
    {
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (Engines engine : Engines.values())
        {
            ItemStack stack = engine.newModuleStack();
            if (stack != null)
                list.add(stack);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        AbstractModule module = getModule(stack);
        if (module != null)
        {
            return module.getUnlocaizedName();
        }
        return super.getUnlocalizedName(stack);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        Engines engine = Engines.get(meta);
        if (engine != null)
        {
            return engine.icon;
        }
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(ICBM.PREFIX + "rocket.motor");
        for (Engines engine : Engines.values())
        {
            engine.icon = reg.registerIcon(ICBM.PREFIX + engine.name);
        }
    }

    @Override
    public AbstractModule getModule(ItemStack stack)
    {
        return MissileModuleBuilder.INSTANCE.build(stack);
    }
}
