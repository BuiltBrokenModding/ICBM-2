package com.builtbroken.icbm.content.missile.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Item for missile tile, used mainly to prevent the item from being picked up
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class ItemBlockMissile extends ItemBlock
{
    public ItemBlockMissile(Block block)
    {
        super(block);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean b)
    {
        if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
        {
            ((EntityPlayer) entity).inventory.setInventorySlotContents(slot, null);
            ((EntityPlayer) entity).inventoryContainer.detectAndSendChanges();
        }
    }
}
