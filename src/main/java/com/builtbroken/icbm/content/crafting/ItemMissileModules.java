package com.builtbroken.icbm.content.crafting;

import com.builtbroken.icbm.api.IModuleItem;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import java.util.List;

/**
 * Item for the modules to be contained in an inventory
 * Created by robert on 12/28/2014.
 */
public class ItemMissileModules extends Item implements IModuleItem, IItemRenderer
{
    public ItemMissileModules()
    {
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    public ItemStack getModuleStackForModule(String module_id)
    {
        if (MissileModuleBuilder.INSTANCE.isRegistered(module_id))
        {
            ItemStack stack = new ItemStack(this);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString(ModuleBuilder.SAVE_ID, module_id);
            return stack;
        }
        return null;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        AbstractModule module = getModule(stack);
        if (module != null)
        {
            return module.getIcon(stack, pass);
        }
        return super.getIcon(stack, pass);
    }


    @Override
    public AbstractModule getModule(ItemStack stack)
    {
        return MissileModuleBuilder.INSTANCE.build(stack);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (String id : MissileModuleBuilder.INSTANCE.getIDs())
        {
            if(MissileModuleBuilder.INSTANCE.idToUseWithModuleItem.contains(id))
            {
                ItemStack stack = getModuleStackForModule(id);
                if (stack != null)
                {
                    list.add(stack);
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        AbstractModule module = getModule(item);
        if (module instanceof IItemRenderer)
        {
            return ((IItemRenderer) module).handleRenderType(item, type);
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        AbstractModule module = getModule(item);
        if (module instanceof IItemRenderer)
        {
            return ((IItemRenderer) module).shouldUseRenderHelper(type, item, helper);
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        AbstractModule module = getModule(item);
        if (module instanceof IItemRenderer)
        {
            ((IItemRenderer) module).renderItem(type, item, data);
        }
    }
}
