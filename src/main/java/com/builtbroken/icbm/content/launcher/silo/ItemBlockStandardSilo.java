package com.builtbroken.icbm.content.launcher.silo;

import com.builtbroken.icbm.content.prefab.ItemBlockICBM;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/19/2016.
 */
public class ItemBlockStandardSilo extends ItemBlockICBM
{
    public ItemBlockStandardSilo(Block block)
    {
        super(block);
        additionalHeight = 6;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        lines.add(Colors.RED.toString() + "!!" + LanguageUtility.getLocal("text.general.feature.wip") + "!!");
        super.addInformation(stack, player, lines, b);
    }
}
