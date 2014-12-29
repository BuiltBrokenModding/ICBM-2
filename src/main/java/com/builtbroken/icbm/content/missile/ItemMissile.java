package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IAmmo;
import com.builtbroken.icbm.api.IAmmoType;
import com.builtbroken.icbm.api.IWeapon;
import com.builtbroken.icbm.content.crafting.missile.MissileSizes;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import resonant.api.explosive.IExplosive;
import resonant.api.items.IExplosiveItem;
import resonant.lib.utility.LanguageUtility;
import resonant.lib.world.explosive.ExplosiveRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Item version of the missile
 *
 * @author Darkguardsman
 */
public class ItemMissile extends Item implements IExplosiveItem, IAmmo
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
        if(item.getItemDamage() < MissileSizes.values().length)
        {
            MissileSizes size = MissileSizes.values()[item.getItemDamage()];
            if (getExplosive(item) == null)
            {
                return getUnlocalizedName() + "." + size.toString().toLowerCase() + ".empty";
            }
            return getUnlocalizedName() + "." + size.toString().toLowerCase();
        }
        return getUnlocalizedName();
    }

    @Override
    public IExplosive getExplosive(ItemStack itemStack)
    {
        Missile missile = MissileSizes.loadMissile(itemStack);
        return missile.getWarhead() != null ? missile.getWarhead().ex : null;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for(MissileSizes size : MissileSizes.values())
        {
            list.add(new ItemStack(item, 1, size.ordinal()));
            for(IExplosive ex: ExplosiveRegistry.getExplosives())
            {
                list.add(MissileModuleBuilder.INSTANCE.buildMissile(size, ex).toStack());
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
    {
        super.addInformation(stack, player, list, bool);
        Missile missile = MissileSizes.loadMissile(stack);
        IExplosive ex = missile.getWarhead() != null ? missile.getWarhead().ex : null;
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
        return getExplosive(stack) != null;
    }

    @Override
    public boolean isClip(ItemStack stack)
    {
        return false;
    }

    @Override
    public IAmmoType getAmmoType(ItemStack stack)
    {
        return AmmoTypeMissile.INSTANCE;
    }

    @Override
    public int getAmmoCount(ItemStack ammoStack)
    {
        return ammoStack.stackSize;
    }

    @Override
    public void fireAmmo(IWeapon weapon, ItemStack ammoStack, Entity firingEntity)
    {
        if (firingEntity instanceof EntityLivingBase)
        {
            EntityMissile.fireMissileByEntity((EntityLivingBase) firingEntity, ammoStack);
        }
        else
        {
            ICBM.LOGGER.error("Item Missile can't be fired using \n fireAmmo(" + weapon + ", " + ammoStack + ", " + firingEntity + ") \n when the entity is not an instanceof EntityLivingBase");
        }
    }

    @Override
    public void consumeAmmo(IWeapon weapon, ItemStack ammoStack, int shotsFired)
    {
        ammoStack.stackSize -= shotsFired;
    }
}
