package com.builtbroken.icbm.content.missile.item;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.api.warhead.IWarheadHandler;
import com.builtbroken.icbm.content.missile.data.ammo.AmmoDataMissile;
import com.builtbroken.icbm.content.missile.data.casing.MissileCasingData;
import com.builtbroken.icbm.content.missile.data.missile.Missile;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.engine.Engines;
import com.builtbroken.icbm.content.missile.parts.engine.RocketEngine;
import com.builtbroken.icbm.content.missile.parts.guidance.Guidance;
import com.builtbroken.icbm.content.missile.parts.guidance.GuidanceModules;
import com.builtbroken.icbm.content.missile.parts.warhead.Warhead;
import com.builtbroken.icbm.content.missile.parts.warhead.WarheadCasings;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.explosives.IExplosiveContainerItem;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.api.items.weapons.IItemAmmo;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.client.ExplosiveRegistryClient;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.framework.explosive.ExplosiveRegistry;
import com.builtbroken.mc.framework.item.ItemBase;
import com.builtbroken.mc.lib.data.item.ItemStackWrapper;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Item version of the missile
 *
 * @author Darkguardsman
 */
public class ItemMissile extends ItemBase implements IExplosiveItem, IItemAmmo.IItemAmmoFireHandler, IMissileItem, IModularMissileItem, IExplosiveContainerItem
{
    @SideOnly(Side.CLIENT)
    IIcon emptyIcon;

    public ItemMissile()
    {
        super("missile", ICBM.DOMAIN, "missile");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public String getRenderContentID(ItemStack stack)
    {
        MissileCasingData data = Missile.getMissileCasingData(stack);
        if (data != null)
        {
            return data.getContentID();
        }
        return super.getRenderContentID(stack);
    }

    @Override
    public List<String> getRenderContentIDs()
    {
        List<String> list = new ArrayList();
        list.add(getRenderContentID(0));
        for (MissileSize size : MissileSize.values())
        {
            for (MissileCasingData data : size.casingDataMap.values())
            {
                String id = data.getContentID();
                if (id != null && !iconString.isEmpty())
                {
                    list.add(id);
                }
            }
        }
        return list;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        super.registerIcons(reg);
        emptyIcon = reg.registerIcon(References.PREFIX + "blank");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass)
    {
        if (pass > 0)
        {
            IIcon icon = ExplosiveRegistryClient.getCornerIconFor(stack, pass - 1);
            if (icon == null)
            {
                return emptyIcon;
            }
            return icon;
        }
        return super.getIcon(stack, pass);
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
        if (item.getItemDamage() < MissileSize.values().length)
        {
            MissileSize size = MissileSize.values()[item.getItemDamage()];
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
        IWarhead warhead = Missile.loadWarheadFromMissileStack(itemStack);
        return warhead != null ? warhead.getExplosive() : null;
    }

    @Override
    public NBTTagCompound getAdditionalExplosiveData(ItemStack itemStack)
    {
        IWarhead warhead = Missile.loadWarheadFromMissileStack(itemStack);
        return warhead != null ? warhead.getAdditionalExplosiveData() : null;
    }

    @Override
    public double getExplosiveSize(ItemStack itemStack)
    {
        IWarhead warhead = Missile.loadWarheadFromMissileStack(itemStack);
        return warhead != null ? warhead.getExplosiveSize() : 0;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (MissileSize size : MissileSize.values())
        {
            getSubItems(size, list);
        }
    }

    /**
     * Gets all the sub items only for the size of the casing. Used
     * in creative tabs only
     *
     * @param size
     * @param list
     */
    public static void getSubItems(MissileSize size, List list)
    {
        if (!size.casingDataMap.isEmpty())
        {
            //Add empty casings to GUI
            for (MissileCasingData data : size.casingDataMap.values())
            {
                list.add(new Missile(data).toStack());
            }
            //Loop all explosives
            for (IExplosiveHandler ex : ExplosiveRegistry.getExplosives())
            {
                List<ItemStackWrapper> items = ExplosiveRegistry.getItems(ex);
                if (items != null)
                {
                    //Loop all explosives for handler
                    for (ItemStackWrapper wrapper : items)
                    {
                        //Create warhead
                        Warhead warhead = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.values()[size.ordinal()], wrapper.itemStack);
                        warhead.explosive.stackSize = warhead.getMaxExplosives();

                        //Create missile
                        Missile missile = new Missile(size.getDefaultMissileCasing());

                        if (missile.data == null)
                        {
                            //System.out.println();
                        }
                        //Set warhead
                        missile.setWarhead(warhead);

                        //Set engine
                        missile.setEngine(Engines.CREATIVE_ENGINE.newModule());

                        //Set guidance
                        missile.setGuidance(GuidanceModules.CHIP_THREE.newModule());

                        //Add to list
                        list.add(missile.toStack());
                    }
                }
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
    {
        super.addInformation(stack, player, list, bool);
        IMissile missile = toMissile(stack);
        if (missile != null)
        {
            //Guidance localization
            String guidance_translation = LanguageUtility.getLocal("info." + ICBM.PREFIX + "guidance.name") + ": ";
            if (missile.getEngine() != null)
            {
                guidance_translation += LanguageUtility.getLocal(missile.getGuidance().getUnlocalizedName() + ".name");
            }
            else
            {
                guidance_translation += "----";
            }

            list.add(guidance_translation);

            //Engine localization
            String engine_translation = LanguageUtility.getLocal("info." + ICBM.PREFIX + "engine.name") + ": ";
            if (missile.getEngine() != null)
            {
                engine_translation += LanguageUtility.getLocal(missile.getEngine().getUnlocalizedName() + ".name");
            }
            else
            {
                engine_translation += "----";
            }
            list.add(engine_translation);


            IExplosiveHandler ex = missile.getWarhead() != null ? missile.getWarhead().getExplosive() : null;
            if (ex != null)
            {
                List<String> l = new ArrayList();
                if (ex instanceof IWarheadHandler)
                {
                    ((IWarheadHandler) ex).addInfoToItem(player, missile.getWarhead(), l);
                }
                else
                {
                    ex.addInfoToItem(player, missile.getWarhead().toStack(), l);
                }

                for (String s : l)
                {
                    list.add(s);
                }
            }
            else
            {
                String ex_translation = LanguageUtility.getLocal("info." + References.PREFIX + "explosive.name") + ": ";
                ex_translation += "----";
                list.add(ex_translation);
            }
        }
        else
        {
            list.add(Colors.RED.code + "Error broken NBT data");
            list.add(Colors.RED.code + " Place in crafting grid to fix");
        }
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        IMissile missile = toMissile(stack);
        return missile != null && missile.canLaunch();
    }

    @Override
    public boolean isClip(ItemStack stack)
    {
        return false;
    }

    @Override
    public IAmmoData getAmmoData(ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
            case 1:
                return AmmoDataMissile.SMALL;
            case 2:
                return AmmoDataMissile.STANDARD;
            case 3:
                return AmmoDataMissile.MEDIUM;
            case 4:
                return AmmoDataMissile.LARGE;
        }
        return AmmoDataMissile.MICRO;
    }

    @Override
    public int getAmmoCount(ItemStack ammoStack)
    {
        return ammoStack.stackSize;
    }

    @Override
    public void fireAmmo(IItemReloadableWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, Entity firingEntity)
    {
        EntityMissile.fireMissileByEntity(firingEntity, ammoStack);
    }

    @Override
    public void consumeAmmo(IItemReloadableWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, int shotsFired)
    {
        ammoStack.stackSize -= shotsFired;
    }

    @Override
    public Entity getMissileEntity(ItemStack stack)
    {
        EntityMissile missile = new EntityMissile((World) null);
        missile.setMissile(toMissile(stack));
        return missile;
    }

    @Override
    public Entity getMissileEntity(ItemStack stack, Entity firedBy)
    {
        if (firedBy instanceof EntityLivingBase)
        {
            EntityMissile missile = new EntityMissile((EntityLivingBase) firedBy);
            missile.setMissile(toMissile(stack));
            return missile;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        if (pass > 0)
        {
            return ExplosiveRegistryClient.getColorForCornerIcon(stack, pass - 1);
        }
        return super.getColorFromItemStack(stack, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata)
    {
        return ExplosiveRegistryClient.renderPassesForItem;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    public ItemStack getEngine(ItemStack stack)
    {
        //TODO Directly access stack to increase performance
        IMissile missile = toMissile(stack);
        return missile != null && missile.getEngine() != null ? missile.getEngine().toStack() : null;
    }

    @Override
    public boolean setEngine(ItemStack m_stack, ItemStack stack, boolean simulate)
    {
        IMissile missile = toMissile(m_stack);
        if (missile != null)
        {
            if (missile.getEngine() == null)
            {
                if (stack != null && stack.getItem() instanceof IModuleItem)
                {
                    IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
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
        IMissile missile = toMissile(stack);
        return missile != null && missile.getWarhead() != null ? missile.getWarhead().toStack() : null;
    }

    @Override
    public boolean setWarhead(ItemStack m_stack, ItemStack stack, boolean simulate)
    {
        IMissile missile = new Missile(m_stack);
        if (missile != null)
        {
            if (missile.getWarhead() == null && stack != null && stack.getItem() instanceof IModuleItem)
            {
                IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
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
        IMissile missile = toMissile(stack);
        return missile != null && missile.getGuidance() != null ? missile.getGuidance().toStack() : null;
    }

    @Override
    public boolean setGuidance(ItemStack m_stack, ItemStack stack, boolean simulate)
    {
        IMissile missile = new Missile(m_stack);
        if (missile != null)
        {
            if (missile.getGuidance() == null && stack != null && stack.getItem() instanceof IModuleItem)
            {
                IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
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

    @Override
    public ItemStack getExplosiveStack(ItemStack stack)
    {
        IMissile missile = toMissile(stack);
        if (missile != null && missile.getWarhead() != null)
        {
            return missile.getWarhead().getExplosiveStack();
        }
        return null;
    }

    @Override
    public boolean setExplosiveStack(ItemStack stack, ItemStack explosive)
    {
        IMissile missile = toMissile(stack);
        if (missile != null && missile.getWarhead() != null)
        {
            return missile.getWarhead().setExplosiveStack(explosive);
        }
        return false;
    }

    @Override
    public IMissile toMissile(ItemStack stack)
    {
        return new Missile(stack);
    }
}
