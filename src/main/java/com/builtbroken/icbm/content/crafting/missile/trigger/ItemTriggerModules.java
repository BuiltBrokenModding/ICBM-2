package com.builtbroken.icbm.content.crafting.missile.trigger;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.warhead.ITrigger;
import com.builtbroken.icbm.content.crafting.missile.ItemAbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.trigger.impact.ImpactTrigger;
import com.builtbroken.jlib.data.science.units.UnitDisplay;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Item for the modules to be contained in an inventory
 * Created by robert on 12/28/2014.
 */
public class ItemTriggerModules extends ItemAbstractModule implements IPostInit
{
    public ItemTriggerModules()
    {
        this.setHasSubtypes(true);
        this.setUnlocalizedName(ICBM.PREFIX + "triggers");
    }

    @Override
    public void onPostInit()
    {

    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        //TODO translate
        IModule module = getModule(stack);
        if (module instanceof ImpactTrigger)
        {
            //TODO replace with translation keys
            list.add("Kinetic Energy Trigger Values");
            list.add("Min: " + new UnitDisplay(UnitDisplay.Unit.JOULES, ((ImpactTrigger) module).getMinimalForce(), true));
            list.add("Max: " + new UnitDisplay(UnitDisplay.Unit.JOULES, ((ImpactTrigger) module).getMaximalForce(), true));
        }
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (Triggers engine : Triggers.values())
        {
            list.add(engine.newModuleStack());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        return Triggers.get(meta) != null ? Triggers.get(meta).icon : this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (Triggers trigger : Triggers.values())
        {
            trigger.icon = reg.registerIcon(ICBM.PREFIX + trigger.moduleName);
        }
    }

    @Override
    public ITrigger getModule(ItemStack stack)
    {
        if (stack != null)
        {
            ItemStack insert = stack.copy();
            insert.stackSize = 1;
            ITrigger trigger = MissileModuleBuilder.INSTANCE.buildTrigger(insert);
            if (trigger == null)
            {
                trigger = Triggers.get(insert).newModule();
                stack.setTagCompound(trigger.save(new NBTTagCompound()));
                insert.setTagCompound(trigger.save(new NBTTagCompound()));
            }
            return trigger;
        }
        return null;
    }
}
