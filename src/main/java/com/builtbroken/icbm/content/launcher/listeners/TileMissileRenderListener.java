package com.builtbroken.icbm.content.launcher.listeners;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.launcher.TileMissileContainer;
import com.builtbroken.mc.api.tile.listeners.IBlockListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListener;
import com.builtbroken.mc.api.tile.listeners.ITileEventListenerBuilder;
import com.builtbroken.mc.api.tile.listeners.client.ITileRenderListener;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.json.loading.JsonProcessorData;
import com.builtbroken.mc.prefab.tile.listeners.TileListener;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles rendering missiles on a tile
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/17/2017.
 */
public class TileMissileRenderListener extends TileListener implements IBlockListener, ITileRenderListener
{
    @JsonProcessorData(value = "renderOffset", type = "pos")
    public Pos offset = Pos.zero;
    @JsonProcessorData(value = "renderRotation", type = "eulerAngle")
    public EulerAngle rotation = new EulerAngle(0, 0, 0);

    @Override
    public void renderDynamic(TileEntity tile, double xx, double yy, double zz, float f)
    {
        ITileNode node = tile instanceof ITileNodeHost ? ((ITileNodeHost) tile).getTileNode() : null;

        if (node instanceof TileMissileContainer)
        {
            //Render missile
            IMissile missile = ((TileMissileContainer) node).getMissile();
            if (missile instanceof ICustomMissileRender)
            {
                GL11.glPushMatrix();
                double h = missile.getHeight() / 2;
                GL11.glTranslated(xx + offset.x(), yy + offset.y() + h, zz + offset.z());
                //TODO rotate
                ((ICustomMissileRender) ((TileMissileContainer) node).getMissile()).renderMissileInWorld(0, 0, f);
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
            return new TileMissileRenderListener();
        }

        @Override
        public String getListenerKey()
        {
            return "missileRender";
        }
    }
}
