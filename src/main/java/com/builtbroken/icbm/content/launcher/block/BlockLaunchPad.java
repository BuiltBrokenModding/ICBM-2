package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.ICBM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Concrete launch pad entirely used for visuals and nothing else....
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/4/2015.
 */
public final class BlockLaunchPad extends Block
{
    @SideOnly(Side.CLIENT)
    IIcon top;
    @SideOnly(Side.CLIENT)
    IIcon bot;
    @SideOnly(Side.CLIENT)
    IIcon left;
    @SideOnly(Side.CLIENT)
    IIcon right;
    @SideOnly(Side.CLIENT)
    IIcon lower_left;
    @SideOnly(Side.CLIENT)
    IIcon lower_right;
    @SideOnly(Side.CLIENT)
    IIcon upper_left;
    @SideOnly(Side.CLIENT)
    IIcon upper_right;

    public BlockLaunchPad()
    {
        super(Material.rock);
        this.setResistance(20);
        this.setHardness(20);
        this.setBlockName(ICBM.PREFIX + "decorLaunchPad");
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(ICBM.PREFIX + "pad.center");
        this.top = reg.registerIcon(ICBM.PREFIX + "pad.top");
        this.bot = reg.registerIcon(ICBM.PREFIX + "pad.bot");
        this.left = reg.registerIcon(ICBM.PREFIX + "pad.left");
        this.right = reg.registerIcon(ICBM.PREFIX + "pad.right");
        this.lower_left = reg.registerIcon(ICBM.PREFIX + "pad.lower.left");
        this.lower_right = reg.registerIcon(ICBM.PREFIX + "pad.lower.right");
        this.upper_left = reg.registerIcon(ICBM.PREFIX + "pad.upper.left");
        this.upper_right = reg.registerIcon(ICBM.PREFIX + "pad.upper.right");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == 1)
        {
            switch (meta)
            {
                case 0:
                    return top;
                case 1:
                    return bot;
                case 2:
                    return left;
                case 3:
                    return right;
                case 4:
                    return lower_left;
                case 5:
                    return lower_right;
                case 6:
                    return upper_left;
                case 7:
                    return upper_right;
            }
        }
        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        for (int i = 0; i < 9; i++)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }
}
