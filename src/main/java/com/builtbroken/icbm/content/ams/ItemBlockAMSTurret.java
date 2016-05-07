package com.builtbroken.icbm.content.ams;

import com.builtbroken.icbm.content.prefab.ItemBlockICBM;
import com.builtbroken.jlib.data.Colors;
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
public class ItemBlockAMSTurret extends ItemBlockICBM
{
    public ItemBlockAMSTurret(Block p_i45328_1_)
    {
        super(p_i45328_1_);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        super.addInformation(stack, player, list, b);
        list.add(Colors.RED.code + "Client rotation is slightly faster than server!");
    }
}
