package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
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
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Mainly a placeholder block for creating launchers. In other words it can be used as a decoration as it has very little functionality.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/4/2015.
 */
public class BlockLauncherPart extends Block
{
    @SideOnly(Side.CLIENT)
    IIcon cpuTop;

    public BlockLauncherPart()
    {
        super(Material.iron);
        this.setBlockName(ICBM.PREFIX + "launcherPart");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy, float zz)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 && WrenchUtility.isHoldingWrench(player) && player.isSneaking() && side != 0 && side != 1)
        {
            if (!world.isRemote)
            {
                //Detects all launcher frame blocks above it(up to max)
                int count = 0;
                Block block = world.getBlock(x, y + 1, z);
                while (count < 5 && block == ICBM.blockLauncherFrame)
                {
                    count++;
                    block = world.getBlock(x, y + count, z);
                    Pos pos = new Pos(x, y + count, z).add(ForgeDirection.getOrientation(side));
                    if (!pos.isAirBlock(world))
                    {
                        player.addChatComponentMessage(new ChatComponentText("To prevent issues clear the blocks from the side of the tower that the missile will occupy."));
                        return true;
                    }
                }
                if (count == 5)
                {
                    //create standard launcher
                    new Pos(x, y, z).setBlock(world, ICBM.blockStandardLauncher, side);
                    //TODO add translation key
                    player.addChatComponentMessage(new ChatComponentText("Standard launcher created"));
                }
                else if (count > 5)
                {
                    //TODO add translation key
                    player.addChatComponentMessage(new ChatComponentText("Detected " + (count - 5) + " extra tower blocks. You need only 5 for standard launcher setup."));
                }
                else if (count < 5)
                {
                    //TODO add translation key
                    player.addChatComponentMessage(new ChatComponentText("Detected " + (5 - count) + " missing tower blocks. You need 5 for standard launcher setup."));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(ICBM.PREFIX + "launcher.box");
        this.cpuTop = reg.registerIcon(ICBM.PREFIX + "launcher.box.top");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (meta == 0)
        {
            if (side == 1)
            {
                return cpuTop;
            }
            return this.blockIcon;
        }
        return Blocks.hopper.getIcon(side, meta);
    }
}
