package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.api.warhead.IWarheadHandler;
import com.builtbroken.icbm.api.warhead.IWarheadItem;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.client.ExplosiveRegistryClient;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlockWarhead extends ItemBlock implements IWarheadItem
{
    @SideOnly(Side.CLIENT)
    IIcon emptyIcon;

    public ItemBlockWarhead(Block block)
    {
        super(block);
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        super.registerIcons(reg);
        for (WarheadCasings casing : WarheadCasings.values())
        {
            casing.icon = reg.registerIcon(ICBM.PREFIX + "warhead." + casing.name().replace("EXPLOSIVE_", "").toLowerCase());
        }
        emptyIcon = reg.registerIcon(References.PREFIX + "blank");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber()
    {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta >= 0 && meta < WarheadCasings.values().length)
        {
            return WarheadCasings.values()[meta].icon;
        }
        return Items.apple.getIconFromDamage(meta);
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
            return ExplosiveRegistryClient.getColorForCornerIcon(stack, pass - 1);
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
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + ICBM.PREFIX + "warhead";
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName() + "." + WarheadCasings.get(stack.getItemDamage()).name().replace("EXPLOSIVE_", "").toLowerCase();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        super.addInformation(stack, player, lines, b);
        IWarhead warhead = getModule(stack);
        if (warhead != null)
        {
            IExplosiveHandler ex = warhead.getExplosive();
            if (ex instanceof IWarheadHandler)
            {
                ((IWarheadHandler) ex).addInfoToItem(player, warhead, lines);
            }
            else
            {
                ExplosiveItemUtility.addInformation(stack, ex, player, lines, b);
            }
        }
        else
        {
            lines.add(LanguageUtility.getLocalName("warhead.error.missing_data"));
        }
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
    {
        //TODO evil laugh, allow the player to eat the bomb
        return stack;
    }


    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int t, boolean b)
    {
        //TODO if the entity is on fire blow up
        //TODO if timer is running and ends blow up
    }

    //TODO allow throwing the explosive
    @Override
    public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack p_77626_1_)
    {
        return 0;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, EntityPlayer p_77615_3_, int p_77615_4_)
    {
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
        //TODO hot potato
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        //TODO hot potato, and timer duration left
        return stack.isItemDamaged();
    }

    //TODO spawn custom entity to allow triggering the blast code if something happens to the entity
    @Override
    public int getEntityLifespan(ItemStack itemStack, World world)
    {
        return 6000;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack)
    {
        return false;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        return null;
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem)
    {
        return false;
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        //TODO allow wearing the warhead as a hat
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        return null;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        //TODO maybe block swing?
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass)
    {
        //TODO if we implement enchanted missile use this to denote the item has an enchantment
        return super.hasEffect(stack, pass);
    }

    @Override
    public IWarhead getModule(ItemStack stack)
    {
        if (stack != null)
        {
            ItemStack insert = stack.copy();
            insert.stackSize = 1;
            IWarhead warhead = MissileModuleBuilder.INSTANCE.buildWarhead(insert);

            if (warhead == null)
            {
                warhead = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.fromMeta(insert.getItemDamage()), (ItemStack) null);
            }

            warhead.save(insert);
            warhead.save(stack);
            return warhead;
        }
        return null;
    }

    @Override
    public NBTTagCompound getAdditionalExplosiveData(ItemStack stack)
    {
        ItemStack explosive = getExplosiveStack(stack);
        if (explosive != null && explosive.getItem() instanceof IExplosiveItem)
        {
            return ((IExplosiveItem) explosive.getItem()).getAdditionalExplosiveData(explosive);
        }
        return null;
    }

    @Override
    public double getExplosiveSize(ItemStack stack)
    {
        ItemStack explosive = getExplosiveStack(stack);
        if (explosive != null)
        {
            return ExplosiveRegistry.getExplosiveSize(explosive);
        }
        return 0;
    }

    @Override
    public IExplosiveHandler getExplosive(ItemStack stack)
    {
        ItemStack explosive = getExplosiveStack(stack);
        if (explosive != null)
        {
            return ExplosiveRegistry.get(explosive);
        }
        return null;
    }

    @Override
    public ItemStack getExplosiveStack(ItemStack stack)
    {
        return Warhead.loadExplosiveItemFromNBT(stack.getTagCompound());
    }

    @Override
    public boolean setExplosiveStack(ItemStack stack, ItemStack explosive)
    {
        IWarhead warhead = getModule(stack);
        if (warhead != null && !InventoryUtility.stacksMatchExact(getExplosiveStack(stack), explosive))
        {
            warhead.setExplosiveStack(explosive);
            warhead.save(stack.getTagCompound());
            return true;
        }
        return false;
    }
}
