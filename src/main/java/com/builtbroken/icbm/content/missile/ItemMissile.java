package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IAmmo;
import com.builtbroken.icbm.api.IAmmoType;
import com.builtbroken.icbm.api.IWeapon;
import com.builtbroken.icbm.content.crafting.missile.EnumModule;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.engine.Engine;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import resonant.api.explosive.IExplosive;
import resonant.api.items.IExplosiveItem;
import resonant.lib.world.explosive.ExplosiveItemUtility;
import resonant.lib.world.explosive.ExplosiveRegistry;

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
        if(getExplosive(item) == null)
        {
            return getUnlocalizedName() + ".empty";
        }
        return getUnlocalizedName();
    }

    @Override
    public IExplosive getExplosive(ItemStack itemStack)
    {
        return ExplosiveItemUtility.getExplosive(itemStack);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for(int i =0; i < 4; i++)
        {
            list.add(new ItemStack(item, 1, i));
            for(IExplosive ex: ExplosiveRegistry.getExplosives())
            {
                ItemStack stack = new ItemStack(item, 1, i);
                //Build Missile to save
                Missile missile = new Missile(stack);
                //Engine
                missile.setEngine((Engine)EnumModule.CREATIVE_ENGINE.newModule());
                //Warhead

                list.add(stack);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
    {
        super.addInformation(stack, player, list, bool);
        ExplosiveItemUtility.addInformation(stack, player, list, bool);
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
