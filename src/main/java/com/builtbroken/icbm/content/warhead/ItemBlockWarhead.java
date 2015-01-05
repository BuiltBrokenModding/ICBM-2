package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.ICBM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import com.builtbroken.api.explosive.IExplosive;
import com.builtbroken.api.items.IExplosiveItem;
import com.builtbroken.lib.world.explosive.ExplosiveItemUtility;

import java.util.List;

public class ItemBlockWarhead extends ItemBlock implements IExplosiveItem
{
    public ItemBlockWarhead(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile."+ ICBM.PREFIX + "warhead";
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = getMetadata(stack.getItemDamage());
        if(meta == 1)
        {
            return getUnlocalizedName() + ".casing";
        }
        return getUnlocalizedName();
    }

    @Override
    public IExplosive getExplosive(ItemStack itemStack)
    {
        return ExplosiveItemUtility.getExplosive(itemStack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        super.addInformation(stack, player, lines, b);
        ExplosiveItemUtility.addInformation(stack, player, lines, b);
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
    {
        //TODO evil laugh, allow the player to eat the bomb
        return stack;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity)
    {
        //TODO allow placing on entities
        return false;
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

    @SideOnly(Side.CLIENT) @Override
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
}
