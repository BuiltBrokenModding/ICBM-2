package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.launcher.launcher.TileStandardLauncher;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.mc.api.tile.listeners.IBlockListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListenerBuilder;
import com.builtbroken.mc.api.tile.listeners.IWrenchListener;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.tile.listeners.TileListener;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/2/2017.
 */
public class LauncherPartListener extends TileListener implements IWrenchListener, IBlockListener
{
    public static final int MICRO_LAUNCHER_HEIGHT = 1;
    public static final int SMALL_LAUNCHER_HEIGHT = 2;
    public static final int STANDARD_LAUNCHER_HEIGHT = 5;
    public static final int MEDIUM_LAUNCHER_HEIGHT = 17;

    @Override
    public boolean onPlayerRightClickWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (world() != null)
        {
            int meta = getBlockMeta();
            //Launcher CPU
            if (meta == 0 && player.isSneaking() && side != 0 && side != 1)
            {
                if (isServer())
                {
                    final Pos frameStart = new Pos(xi(), yi() + 1, zi());
                    //Detects all launcher frame blocks above it(up to max)
                    int frameCount = getFrameCount(world(), frameStart);

                    //Check if area is clear for missile
                    if (!isPathClear(world(), frameStart, frameCount, side))
                    {
                        //TODO add translation key
                        player.addChatComponentMessage(new ChatComponentText("To prevent issues clear the blocks from the side of the tower that the missile will occupy. micro, small, and standard need 1x1 block space. Medium needs 3x3 block space to be placed."));
                        return true;
                    }

                    MissileSize missileCount = getLauncherSize(frameCount);

                    //Error if size not found
                    if (missileCount == null)
                    {
                        //TODO add translation key
                        player.addChatComponentMessage(new ChatComponentText("Detected " + frameCount + " blocks, micro requires 1, small 2, standard 5, medium 17"));
                        return true;
                    }
                    //Place block and set size
                    else
                    {
                        Block launcherBlock = ICBM_API.blockStandardLauncher;
                        //create standard launcher
                        if (new Pos(this).setBlock(world(), launcherBlock, side))
                        {
                            //TODO add translation key
                            player.addChatComponentMessage(new ChatComponentText(LanguageUtility.capitalizeFirst(missileCount.name().toLowerCase()) + " launcher created"));

                            TileEntity tile = getTileEntity();
                            if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileStandardLauncher)
                            {
                                ((TileStandardLauncher) ((ITileNodeHost) tile).getTileNode()).missileSize = missileCount;
                            }
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText("Unexpected error changing CPU block to standard launcher block."));
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static MissileSize getLauncherSize(int frameCount)
    {
        //Detect size
        if (frameCount == MEDIUM_LAUNCHER_HEIGHT)
        {
            return MissileSize.MEDIUM;
        }
        else if (frameCount == STANDARD_LAUNCHER_HEIGHT)
        {
            return MissileSize.STANDARD;
        }
        else if (frameCount == SMALL_LAUNCHER_HEIGHT)
        {
            return MissileSize.SMALL;
        }
        else if (frameCount == MICRO_LAUNCHER_HEIGHT)
        {
            return MissileSize.MICRO;
        }
        return null;
    }

    public static int getFrameCount(World world, Pos start)
    {
        int count = 0;
        Block block = world.getBlock(start.xi(), start.yi(), start.zi());
        while (count < 255 && block == ICBM_API.blockLauncherFrame)
        {
            //Increase count
            count++;
            //Get next block above last
            block = world.getBlock(start.xi(), start.yi() + count, start.zi());

        }
        return count;
    }

    public static boolean isPathClear(World world, Pos start, int height, int side)
    {
        for (int i = 0; i < height; i++)
        {
            if (height == MEDIUM_LAUNCHER_HEIGHT)
            {
                if (!new Pos(start.xi(), start.yi() + i, start.zi()).add(ForgeDirection.getOrientation(side)).isAirBlock(world))
                {
                    return false;
                }
            }
            else
            {
                for (int x = 0; x < 3; x++)
                {
                    for (int z = 0; z < 3; z++)
                    {
                        if (!new Pos(start.xi() + x, start.yi() + i, start.zi() + z).add(ForgeDirection.getOrientation(side)).isAirBlock(world))
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> getListenerKeys()
    {
        List<String> list = new ArrayList();
        list.add("activation");
        list.add("wrench");
        return list;
    }

    public static class Builder implements ITileEventListenerBuilder
    {
        @Override
        public ITileEventListener createListener(Block block)
        {
            return new LauncherPartListener();
        }

        @Override
        public String getListenerKey()
        {
            return "launcherPartListener";
        }
    }
}
