package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IAmmo;
import com.builtbroken.icbm.api.IAmmoType;
import com.builtbroken.icbm.api.IMissileItem;
import com.builtbroken.icbm.api.IWeapon;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.IExplosiveItem;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Item version of the missile
 *
 * @author Darkguardsman
 */
public class ItemMissile extends Item implements IExplosiveItem, IAmmo, IMissileItem
{
    public ItemMissile()
    {
        this.setUnlocalizedName("missile");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
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
        if(item.getItemDamage() < MissileCasings.values().length)
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
        for(MissileCasings size : MissileCasings.values())
        {
            if(size.enabled)
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
        if(ex != null)
        {
            ex_translation += LanguageUtility.getLocal(ex.getTranslationKey() +".name");
            list.add(ex_translation);

            List<String> l = new ArrayList();
            ex.addInfoToItem(stack, l);
            for(String s : l)
                list.add(s);
        }
        else
        {
            ex_translation += "----";
            list.add(ex_translation);
        }

        String engine_translation = LanguageUtility.getLocal("info." + ICBM.PREFIX + "engine.name") +": ";
        if(missile.getEngine() != null)
        {
            engine_translation += LanguageUtility.getLocal(missile.getEngine().getUnlocaizedName() +".name");
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
            case 1: return AmmoTypeMissile.SMALL;
            case 2: return AmmoTypeMissile.STANDARD;
            case 3: return AmmoTypeMissile.MEDIUM;
            case 4: return AmmoTypeMissile.LARGE;
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
        EntityMissile missile = new EntityMissile((World)null);
        missile.setMissile(MissileModuleBuilder.INSTANCE.buildMissile(stack));
        return missile;
    }

    @Override
    public Entity getMissileEntity(ItemStack stack, Entity firedBy)
    {
        if(firedBy instanceof EntityLivingBase)
        {
            EntityMissile missile = new EntityMissile((EntityLivingBase)firedBy);
            missile.setMissile(MissileModuleBuilder.INSTANCE.buildMissile(stack));
            return missile;
        }
        return null;
    }
}
