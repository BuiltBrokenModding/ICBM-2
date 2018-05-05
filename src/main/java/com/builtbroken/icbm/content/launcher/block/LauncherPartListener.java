package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.launcher.launcher.TileStandardLauncher;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.data.Direction;
import com.builtbroken.mc.framework.block.imp.IBlockListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListenerBuilder;
import com.builtbroken.mc.framework.block.imp.IWrenchListener;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.seven.framework.block.listeners.TileListener;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;

/** Handles wrench support for block
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
        Direction facingSide = Direction.getOrientation(side);
        if (world() != null)
        {
            int meta = getBlockMeta();
            //Launcher CPU
            if (meta == 0 && player.isSneaking() && side != 0 && side != 1)
            {
                if (isServer())
                {
                    final BlockPos frameStart = new BlockPos(xi(), yi() + 1, zi());
                    //Detects all launcher frame blocks above it(up to max)
                    int frameCount = getFrameCount(world().unwrap(), frameStart);

                    if(world().unwrap() != DimensionManager.getWorld(world().unwrap().provider.dimensionId))
                    {
                        System.out.println("error");
                    }

                    MissileSize missileSize = getLauncherSize(frameCount);
                    //Error if size not found
                    if (missileSize == null)
                    {
                        //TODO add translation key
                        player.addChatComponentMessage(new ChatComponentText("Detected " + frameCount + " launcher frame blocks, micro requires 1, small 2, standard 5, medium 17"));  //TODO translate
                        return true;
                    }
                    //Place block and set size
                    else
                    {
                        //Check if area is clear for missile
                        if (!isPathClear(world().unwrap(), frameStart.add(facingSide), frameCount, facingSide))
                        {
                            //TODO add translation key
                            player.addChatComponentMessage(new ChatComponentText("To prevent issues clear the blocks from the side of the tower that the missile will " +
                                    "occupy. micro, small, and standard need 1x1 block space. Medium needs 3x3 block space to be placed."));  //TODO translate
                            return true;
                        }

                        Block launcherBlock = ICBM_API.blockStandardLauncher;
                        //create standard launcher
                        if (new Pos(this).setBlock(world().unwrap(), launcherBlock, side))
                        {
                            //TODO add translation key
                            player.addChatComponentMessage(new ChatComponentText(LanguageUtility.capitalizeFirst(missileSize.name().toLowerCase()) + " launcher created")); //TODO translate

                            TileEntity tile = getTileEntity();
                            if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileStandardLauncher)
                            {
                                ((TileStandardLauncher) ((ITileNodeHost) tile).getTileNode()).missileSize = missileSize;
                            }
                            else
                            {
                                player.addChatComponentMessage(new ChatComponentText(Colors.RED.code + "Error: Failed to set missile size for tile, this is a bug.")); //TODO translate
                            }
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText(Colors.RED.code + "Error: Failed to convert CPU block to standard launcher block. " +
                                    "Check console for error messages and world for protection plugins preventing action.")); //TODO translate
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

    public static int getFrameCount(World world, BlockPos start)
    {
        int count = 0;
        Block block = start.getBlock(world);
        while (count < 255 && block == ICBM_API.blockLauncherFrame)
        {
            //Increase count
            count++;
            //Get next block above last
            block = world.getBlock(start.xi(), start.yi() + count, start.zi());

        }
        return count;
    }

    public static boolean isPathClear(World world, BlockPos start, int height, Direction side)
    {
        for (int i = 0; i < height; i++)
        {
            if (height != MEDIUM_LAUNCHER_HEIGHT)
            {
                if (!new BlockPos(start.xi(), start.yi() + i, start.zi()).add(side).isAirBlock(world))
                {
                    return false;
                }
            }
            else
            {
                start = start.add(side);
                for (int x = -1; x < 2; x++)
                {
                    for (int z = -1; z < 2; z++)
                    {
                        if (!new Pos(start.xi() + x, start.yi() + i, start.zi() + z).add(side).isAirBlock(world))
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
