package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.ICBM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

/**
 * Support frame for a missile launcher. IRL it prevents missiles from being blown over in the wind.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
public class BlockLauncherFrame extends Block
{
    public BlockLauncherFrame()
    {
        super(Material.iron);
        this.setBlockName(ICBM.PREFIX + "launcherFrame");
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return Blocks.iron_bars.getIcon(p_149691_1_, p_149691_2_);
    }

    @Override
    public int getRenderType()
    {
        return ISBRLauncherFrame.INSTANCE.ID;
    }
}
