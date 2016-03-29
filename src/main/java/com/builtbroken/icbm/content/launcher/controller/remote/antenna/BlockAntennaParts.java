package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/28/2016.
 */
public class BlockAntennaParts extends Block
{
    protected BlockAntennaParts()
    {
        super(Material.iron);
        this.setBlockName(ICBM.PREFIX + "antennaParts");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy, float zz)
    {
        int meta = world.getBlockMetadata(x, y, z);
        //Antenna for linking missile areas
        if (meta == 0 && WrenchUtility.isHoldingWrench(player) && player.isSneaking() && side != 0 && side != 1)
        {
            if (!world.isRemote)
            {
                int i = 0;
                //Path down to find lowest point
                for (; i >= 0; i--)
                {
                    Block block = world.getBlock(x, i, z);
                    if (block instanceof BlockAntennaParts)
                    {
                        int m = world.getBlockMetadata(x, i, z);
                    }
                    else
                    {
                        break;
                    }
                }
                //TODO get bottom most block and turn into an antenna tile
                //TODO then tell tile to scan structure blocks
            }
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return Blocks.hopper.getIcon(p_149691_1_, p_149691_2_);
    }
}
