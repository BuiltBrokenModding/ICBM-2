package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/6/2016.
 */
public class ItemBlockLaunchPad extends ItemBlockMetadata
{
    public ItemBlockLaunchPad(Block block)
    {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        list.add("Decoration Block");
    }
}
