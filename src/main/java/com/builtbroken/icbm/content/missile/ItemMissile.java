package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.*;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.parts.MissileCraftingParts;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.IExplosiveItem;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.recipe.item.sheetmetal.RecipeSheetMetal;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Item version of the missile
 *
 * @author Darkguardsman
 */
public class ItemMissile extends Item implements IExplosiveItem, IAmmo, IMissileItem, IPostInit, IModularMissileItem
{
    public ItemMissile()
    {
        this.setUnlocalizedName("missile");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public void onPostInit()
    {
        ItemStack micro_missile_empty = MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.MICRO, null, null, null).toStack();
        ItemStack small_missile_empty = MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.SMALL, null, null, null).toStack();
        if (Engine.itemSheetMetal != null && Engine.itemSheetMetalTools != null)
        {
            GameRegistry.addRecipe(new RecipeSheetMetal(micro_missile_empty, " rf", "rcf", " rf", 'c', ItemSheetMetal.SheetMetal.SMALL_CYLINDER.stack(), 'f', ItemSheetMetal.SheetMetal.FIN_MICRO.stack(), 'r', "rodIron"));
            GameRegistry.addRecipe(new RecipeSheetMetal(small_missile_empty, "rrf", "rcf", "rrf", 'c', MissileCraftingParts.SMALL_MISSILE_CASE.stack(), 'f', ItemSheetMetal.SheetMetal.FIN_SMALL.stack(), 'r', "rodIron"));
        }
        else
        {
            GameRegistry.addShapedRecipe(MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.SMALL, ExplosiveRegistry.get("TNT")).toStack(), "ITI", "IAI", "IFI", 'A', Items.arrow, 'I', Items.iron_ingot, 'T', Blocks.tnt, 'F', Blocks.furnace);
        }
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + ICBM.PREFIX + "missile";
    }

    @Override
    public String getUnlocalizedName(ItemStack item)
    {
        if (item.getItemDamage() < MissileCasings.values().length)
        {
            MissileCasings size = MissileCasings.values()[item.getItemDamage()];
            if (getExplosive(item) == null)
            {
                return getUnlocalizedName() + "." + size.toString().toLowerCase() + ".empty";
            }
            return getUnlocalizedName() + "." + size.toString().toLowerCase();
        }
        return getUnlocalizedName();
    }

    @Override
    public IExplosiveHandler getExplosive(ItemStack itemStack)
    {
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(itemStack);
        return missile.getWarhead() != null ? missile.getWarhead().ex : null;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (MissileCasings size : MissileCasings.values())
        {
            if (size.enabled)
            {
                list.add(MissileModuleBuilder.INSTANCE.buildMissile(size, null).toStack());
                for (IExplosiveHandler ex : ExplosiveRegistry.getExplosives())
                {
                    list.add(MissileModuleBuilder.INSTANCE.buildMissile(size, ex).toStack());
                }
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
    {
        super.addInformation(stack, player, list, bool);
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
        IExplosiveHandler ex = missile.getWarhead() != null ? missile.getWarhead().ex : null;
        String ex_translation = LanguageUtility.getLocal("info." + ICBM.PREFIX + "warhead.name") + ": ";
        if (ex != null)
        {
            ex_translation += LanguageUtility.getLocal(ex.getTranslationKey() + ".name");
            list.add(ex_translation);

            List<String> l = new ArrayList();
            ex.addInfoToItem(stack, l);
            for (String s : l)
            { list.add(s); }
        }
        else
        {
            ex_translation += "----";
            list.add(ex_translation);
        }

        String engine_translation = LanguageUtility.getLocal("info." + ICBM.PREFIX + "engine.name") + ": ";
        if (missile.getEngine() != null)
        {
            engine_translation += LanguageUtility.getLocal(missile.getEngine().getUnlocaizedName() + ".name");
        }
        else
        {
            engine_translation += "----";
        }

        list.add(engine_translation);


    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
        return missile != null && missile.getEngine() != null;
    }

    @Override
    public boolean isClip(ItemStack stack)
    {
        return false;
    }

    @Override
    public IAmmoType getAmmoType(ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
            case 1:
                return AmmoTypeMissile.SMALL;
            case 2:
                return AmmoTypeMissile.STANDARD;
            case 3:
                return AmmoTypeMissile.MEDIUM;
            case 4:
                return AmmoTypeMissile.LARGE;
        }
        return AmmoTypeMissile.MICRO;
    }

    @Override
    public int getAmmoCount(ItemStack ammoStack)
    {
        return ammoStack.stackSize;
    }

    @Override
    public void fireAmmo(IWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, Entity firingEntity)
    {
        EntityMissile.fireMissileByEntity(firingEntity, ammoStack);
    }

    @Override
    public void consumeAmmo(IWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, int shotsFired)
    {
        ammoStack.stackSize -= shotsFired;
    }

    @Override
    public Entity getMissileEntity(ItemStack stack)
    {
        EntityMissile missile = new EntityMissile((World) null);
        missile.setMissile(MissileModuleBuilder.INSTANCE.buildMissile(stack));
        return missile;
    }

    @Override
    public Entity getMissileEntity(ItemStack stack, Entity firedBy)
    {
        if (firedBy instanceof EntityLivingBase)
        {
            EntityMissile missile = new EntityMissile((EntityLivingBase) firedBy);
            missile.setMissile(MissileModuleBuilder.INSTANCE.buildMissile(stack));
            return missile;
        }
        return null;
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

    @Override
    public ItemStack getEngine(ItemStack stack)
    {
        //TODO Directly access stack to increase performance
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
        return missile != null && missile.getEngine() != null ? missile.getEngine().toStack() : null;
    }

    @Override
    public boolean setEngine(ItemStack m_stack, ItemStack stack, boolean simulate)
    {
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(m_stack);
        if (missile != null)
        {
            if (missile.getEngine() == null)
            {
                if (stack != null && stack.getItem() instanceof IModuleItem)
                {
                    AbstractModule module = ((IModuleItem) stack.getItem()).getModule(stack);
                    if (module instanceof RocketEngine)
                    {
                        if (!simulate)
                        {
                            missile.setEngine((RocketEngine) module);
                            missile.toStack();
                        }
                        return true;
                    }
                }
            }
            else if (stack == null)
            {
                if (!simulate)
                {
                    missile.setEngine(null);
                    missile.toStack();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getWarhead(ItemStack stack)
    {
        //TODO Directly access stack to increase performance
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
        return missile != null && missile.getWarhead() != null ? missile.getWarhead().toStack() : null;
    }

    @Override
    public boolean setWarhead(ItemStack m_stack, ItemStack stack, boolean simulate)
    {
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(m_stack);
        if (missile != null)
        {
            if (missile.getWarhead() == null && stack != null && stack.getItem() instanceof IModuleItem)
            {
                AbstractModule module = ((IModuleItem) stack.getItem()).getModule(stack);
                if (module instanceof Warhead)
                {
                    if (!simulate)
                    {
                        missile.setWarhead((Warhead) module);
                        m_stack = missile.toStack();
                    }
                    return true;
                }
            }
            else if (stack == null)
            {
                if (!simulate)
                {
                    missile.setWarhead(null);
                    m_stack = missile.toStack();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getGuidance(ItemStack stack)
    {
        //TODO Directly access stack to increase performance
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
        return missile != null && missile.getGuidance() != null ? missile.getGuidance().toStack() : null;
    }

    @Override
    public boolean setGuidance(ItemStack m_stack, ItemStack stack, boolean simulate)
    {
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(m_stack);
        if (missile != null)
        {
            if (missile.getGuidance() == null && stack != null && stack.getItem() instanceof IModuleItem)
            {
                AbstractModule module = ((IModuleItem) stack.getItem()).getModule(stack);
                if (module instanceof Guidance)
                {
                    if (!simulate)
                    {
                        missile.setGuidance((Guidance) module);
                        m_stack = missile.toStack();
                    }
                    return true;
                }
            }
            else if (stack == null)
            {
                if (!simulate)
                {
                    missile.setGuidance(null);
                    m_stack = missile.toStack();
                }
                return true;
            }
        }
        return false;
    }
}
