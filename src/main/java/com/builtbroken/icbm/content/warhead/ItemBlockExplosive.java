package com.builtbroken.icbm.content.warhead;

import resonant.lib.world.explosive.ExplosiveItemUtility;
import resonant.api.items.IExplosiveItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import resonant.api.explosive.IExplosive;

import java.util.List;

public class ItemBlockExplosive extends ItemBlock implements IExplosiveItem
{
    public ItemBlockExplosive(Block block)
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
        return "icbm.explosive";
    }

    @Override
    public IExplosive getExplosive(ItemStack itemStack)
    {
        return ExplosiveItemUtility.getExplosive(itemStack);
    }

    public void setExplosive(ItemStack itemStack, IExplosive ex)
    {
        ExplosiveItemUtility.setExplosive(itemStack, ex);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        super.addInformation(stack, player, lines, b);
        ExplosiveItemUtility.addInformation(stack, player, lines, b);
    }
}
