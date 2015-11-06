package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.content.crafting.missile.ItemAbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.engine.fluid.RocketEngineFluid;
import com.builtbroken.icbm.content.crafting.missile.engine.solid.RocketEngineSolid;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

/**
 * Item for the modules to be contained in an inventory
 * Created by robert on 12/28/2014.
 */
public class ItemEngineModules extends ItemAbstractModule implements IPostInit
{
    public ItemEngineModules()
    {
        this.setHasSubtypes(true);
    }

    @Override
    public void onPostInit()
    {
        for (Engines engine : Engines.values())
        {
            IRocketEngine e = engine.newModule();
            if (e instanceof IPostInit)
            {
                ((IPostInit) e).onPostInit();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        //TODO translate
        IModule module = getModule(stack);
        if (module instanceof IRocketEngine)
        {
            list.add("Speed: " + ((IRocketEngine) module).getSpeed(null));
            list.add("Range: " + ((IRocketEngine) module).getMaxDistance(null));
            if (module instanceof RocketEngineFluid)
            {
                list.add("Fuel: " + ((RocketEngineFluid) module).getFluidAmount() + "/" + ((RocketEngineFluid) module).getCapacity() + "mL");
            }
            else if (module instanceof RocketEngineSolid)
            {
                ItemStack fuel = ((RocketEngineSolid) module).getInventory().getStackInSlot(0);
                if (fuel != null)
                {
                    int max = Math.max(fuel.getMaxStackSize(), ((RocketEngineSolid) module).getInventory().getInventoryStackLimit());
                    list.add("Fuel: " + fuel.getDisplayName() + " " + fuel.stackSize + "/" + max);
                }
                else
                {
                    list.add("Fuel: empty");
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (Engines engine : Engines.values())
        {
            RocketEngine e = MissileModuleBuilder.INSTANCE.buildEngine(engine.newModuleStack());
            list.add(e.toStack().copy());
            e.initFuel();
            list.add(e.toStack());
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean b)
    {
        //TODO maybe blow up engine if player is on fire?
        if (!world.isRemote)
        {
            //Remove creative module from non-creative mode player's
            if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
            {
                if (stack.getItemDamage() == Engines.CREATIVE_ENGINE.ordinal())
                {
                    ((EntityPlayer) entity).inventory.setInventorySlotContents(slot, null);
                    ((EntityPlayer) entity).inventoryContainer.detectAndSendChanges();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        return Engines.get(meta) != null ? Engines.get(meta).icon : this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (Engines engine : Engines.values())
        {
            engine.icon = reg.registerIcon(ICBM.PREFIX + engine.name);
        }
    }
}
