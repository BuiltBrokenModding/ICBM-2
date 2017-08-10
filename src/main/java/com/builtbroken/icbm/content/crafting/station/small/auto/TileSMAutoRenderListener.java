package com.builtbroken.icbm.content.crafting.station.small.auto;

import com.builtbroken.icbm.content.crafting.station.small.TileSmallMissileWorkstationClient;
import com.builtbroken.mc.api.tile.listeners.IBlockListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListenerBuilder;
import com.builtbroken.mc.api.tile.listeners.client.ITileRenderListener;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.framework.block.listeners.TileListener;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles rendering missiles on a tile
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/17/2017.
 */
public class TileSMAutoRenderListener extends TileListener implements IBlockListener, ITileRenderListener
{
    @Override
    public void renderDynamic(TileEntity tile, double xx, double yy, double zz, float f)
    {
        ITileNode node = tile instanceof ITileNodeHost ? ((ITileNodeHost) tile).getTileNode() : null;

        if (node instanceof TileSMAutoCraft)
        {
            //render missile
            if (((TileSMAutoCraft) node).completedMissile != null)
            {
                GL11.glPushMatrix();
                TileSmallMissileWorkstationClient.renderMissile((float) xx, (float) yy, (float) zz, ((TileSMAutoCraft) node).completedMissile, ForgeDirection.UP, ((TileSMAutoCraft) node).getDirection());
                GL11.glPopMatrix();
            }
            else if (((TileSMAutoCraft) node).startedMissile != null)
            {
                GL11.glPushMatrix();
                TileSmallMissileWorkstationClient.renderMissile((float) xx, (float) yy, (float) zz, ((TileSMAutoCraft) node).startedMissile, ForgeDirection.UP, ((TileSMAutoCraft) node).getDirection());
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public List<String> getListenerKeys()
    {
        List<String> list = new ArrayList();
        list.add("tilerender");
        return list;
    }

    public static class Builder implements ITileEventListenerBuilder
    {
        @Override
        public ITileEventListener createListener(Block block)
        {
            return new TileSMAutoRenderListener();
        }

        @Override
        public String getListenerKey()
        {
            return "TileSMAuto";
        }
    }
}
