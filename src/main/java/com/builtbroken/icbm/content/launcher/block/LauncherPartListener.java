package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.mc.api.tile.listeners.IBlockListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListenerBuilder;
import com.builtbroken.mc.api.tile.listeners.IWrenchListener;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.listeners.TileListener;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/2/2017.
 */
public class LauncherPartListener extends TileListener implements IWrenchListener, IBlockListener
{
    public static final int STANDARD_LAUNCHER_HEIGHT = 5;

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
                    //Detects all launcher frame blocks above it(up to max)
                    int count = 0;
                    Block block = getBlock(xi(), yi() + 1, zi());
                    while (count < STANDARD_LAUNCHER_HEIGHT && block == ICBM_API.blockLauncherFrame) //TODO make 5 a constant
                    {
                        //Increase count
                        count++;
                        //Get next block above last
                        block = getBlock(xi(), yi() + count, zi());
                        //Detects for clear path near launcher
                        Pos pos = new Pos(xi(), yi() + count, zi()).add(ForgeDirection.getOrientation(side));
                        if (!pos.isAirBlock(world()))
                        {
                            player.addChatComponentMessage(new ChatComponentText("To prevent issues clear the blocks from the side of the tower that the missile will occupy."));
                            return true;
                        }
                    }
                    if (count == STANDARD_LAUNCHER_HEIGHT) //TODO make 5 a constant
                    {
                        Block launcherBlock = InventoryUtility.getBlock("icbm:standardlauncher"); //TODO cache standard launcher block instance
                        //create standard launcher
                        if (new Pos(this).setBlock(world(), launcherBlock, side))
                        {
                            //TODO add translation key
                            player.addChatComponentMessage(new ChatComponentText("Standard launcher created"));
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText("Unexpected error changing CPU block to standard launcher block."));
                        }
                    }
                    else if (count > STANDARD_LAUNCHER_HEIGHT) //TODO make 5 a constant
                    {
                        //TODO add translation key
                        player.addChatComponentMessage(new ChatComponentText("Detected " + (count - STANDARD_LAUNCHER_HEIGHT) + " extra tower blocks. You need only 5 for standard launcher setup."));
                    }
                    else if (count < STANDARD_LAUNCHER_HEIGHT) //TODO make 5 a constant
                    {
                        //TODO add translation key
                        player.addChatComponentMessage(new ChatComponentText("Detected " + (STANDARD_LAUNCHER_HEIGHT - count) + " missing tower blocks. You need 5 for standard launcher setup."));
                    }
                }
                return true;
            }
        }
        return false;
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
