package com.builtbroken.icbm.content.launcher.launcher;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/4/2015.
 */
public class ItemBlockLauncherPad extends ItemBlock
{
    public ItemBlockLauncherPad(Block p_i45328_1_)
    {
        super(p_i45328_1_);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean b)
    {
        if (!world.isRemote)
        {
            //Remove as the player should not have this item
            if (entity instanceof EntityPlayer)
            {
                ((EntityPlayer) entity).inventory.setInventorySlotContents(slot, null);
                ((EntityPlayer) entity).inventoryContainer.detectAndSendChanges();
            }
        }
    }
}
