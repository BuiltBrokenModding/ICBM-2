package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy, float zz)
    {
        if (WrenchUtility.isHoldingWrench(player))
        {
            if (!world.isRemote)
            {
                int count = 1;
                Block block = world.getBlock(x, y + 1, z);
                while (count <= 15 && block == this)
                {
                    count++;
                    block = world.getBlock(x, y + count + 1, z);
                }
                if (count == 15)
                {
                    //create medium launcher
                    player.addChatComponentMessage(new ChatComponentText("15 blocks"));
                }
                else if (count == 6)
                {
                    //create standard launcher
                    player.addChatComponentMessage(new ChatComponentText("6 blocks"));
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText("Detected only " + count + " tower blocks. You need 6 for standard, and 15 for medium."));
                }
            }
            return true;
        }
        return false;
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
