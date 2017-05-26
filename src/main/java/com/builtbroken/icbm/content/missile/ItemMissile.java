package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.warhead.IWarheadHandler;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.crafting.parts.MissileCraftingParts;
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
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.recipe.item.sheetmetal.RecipeSheetMetal;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
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
public class ItemMissile extends Item implements IExplosiveItem, IItemAmmo.IItemAmmoFireHandler, IMissileItem, IPostInit, IModularMissileItem, IExplosiveContainerItem
{
    @SideOnly(Side.CLIENT)
    IIcon microMissile;

    @SideOnly(Side.CLIENT)
    IIcon smallMissile;

    @SideOnly(Side.CLIENT)
    IIcon standardMissile;

    @SideOnly(Side.CLIENT)
    IIcon emptyIcon;

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
        ItemStack micro_missile_empty = MissileCasings.MICRO.newModuleStack();
        ItemStack small_missile_empty = MissileCasings.SMALL.newModuleStack();
        if (Engine.itemSheetMetal != null && Engine.itemSheetMetalTools != null)
        {
            GameRegistry.addRecipe(new RecipeSheetMetal(micro_missile_empty, " rf", "rcf", " rf", 'c', ItemSheetMetal.SheetMetal.SMALL_CYLINDER.stack(), 'f', ItemSheetMetal.SheetMetal.FIN_MICRO.stack(), 'r', "rodIron"));
            GameRegistry.addRecipe(new RecipeSheetMetal(small_missile_empty, "rrf", "rcf", "rrf", 'c', MissileCraftingParts.SMALL_MISSILE_CASE.stack(), 'f', ItemSheetMetal.SheetMetal.FIN_SMALL.stack(), 'r', "rodIron"));
        }
        else
        {
            GameRegistry.addShapedRecipe(MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.SMALL, new ItemStack(Blocks.tnt)).toStack(), "ITI", "IAI", "IFI", 'A', Items.arrow, 'I', Items.iron_ingot, 'T', Blocks.tnt, 'F', Blocks.furnace);
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
        IMissile missile = MissileModuleBuilder.INSTANCE.buildMissile(itemStack);
        return missile.getWarhead() != null ? missile.getWarhead().getExplosive() : null;
    }

    @Override
    public NBTTagCompound getAdditionalExplosiveData(ItemStack itemStack)
    {
        IMissile missile = MissileModuleBuilder.INSTANCE.buildMissile(itemStack);
        return missile.getWarhead() != null ? missile.getWarhead().getAdditionalExplosiveData() : null;
    }

    @Override
    public double getExplosiveSize(ItemStack itemStack)
    {
        IMissile missile = MissileModuleBuilder.INSTANCE.buildMissile(itemStack);
        return missile.getWarhead() != null ? missile.getWarhead().getExplosiveSize() : 0;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (MissileCasings size : MissileCasings.values())
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
    public static void getSubItems(MissileCasings size, List list)
    {
        if (size.enabled)
        {
            list.add(size.newModuleStack());
            for (IExplosiveHandler ex : ExplosiveRegistry.getExplosives())
            {
                List<ItemStackWrapper> items = ExplosiveRegistry.getItems(ex);
                if (items != null)
                {
                    for (ItemStackWrapper wrapper : items)
                    {
                        Warhead warhead = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.values()[size.ordinal()], wrapper.itemStack);
                        warhead.explosive.stackSize = warhead.getMaxExplosives();
                        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(size, (ItemStack) null);
                        missile.setWarhead(warhead);
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
    public void registerIcons(IIconRegister reg)
    {
        microMissile = reg.registerIcon(ICBM.PREFIX + "micro.missile");
        smallMissile = reg.registerIcon(ICBM.PREFIX + "small.missile");
        standardMissile = reg.registerIcon(ICBM.PREFIX + "standard.missile");
        emptyIcon = reg.registerIcon(References.PREFIX + "blank");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta == 0)
        {
            return microMissile;
        }
        else if (meta == 1)
        {
            return smallMissile;
        }
        else if (meta == 2)
        {
            return standardMissile;
        }
        return microMissile;
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
        return getIconFromDamage(stack.getItemDamage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        if (pass > 0)
        {
            return ExplosiveRegistryClient.getColorForCornerIcon(stack, pass -1);
        }
        return 16777215;
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
        IMissile missile = MissileModuleBuilder.INSTANCE.buildMissile(m_stack);
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
        IMissile missile = MissileModuleBuilder.INSTANCE.buildMissile(m_stack);
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
        IMissile missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
        if (missile == null)
        {
            //TODO if NBT is not null see if we can validate some parts of it
            missile = MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.fromMeta(stack.getItemDamage()), (ItemStack) null);
            stack.setTagCompound(missile.save(new NBTTagCompound())); //Save new valid data
        }
        return missile;
    }
}
