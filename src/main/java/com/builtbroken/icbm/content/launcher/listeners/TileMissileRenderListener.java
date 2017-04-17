package com.builtbroken.icbm.content.launcher.listeners;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.mc.api.tile.listeners.IBlockListener;
import com.builtbroken.mc.api.tile.listeners.client.ITileRenderListener;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.listeners.TileListener;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/**
 * Handles rendering missiles on a tile
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/17/2017.
 */
public class TileMissileRenderListener extends TileListener implements IBlockListener, ITileRenderListener
{
    public Pos offset = Pos.zero;
    public EulerAngle rotation = new EulerAngle(0, 0, 0);

    public TileMissileRenderListener()
    {

    }

    @Override
    public void renderDynamic(TileEntity tile, double xx, double yy, double zz, int pass)
    {
        ITileNode node = tile instanceof ITileNodeHost ? ((ITileNodeHost) tile).getTileNode() : null;

        if (node instanceof TileMissileContainer)
        {
            //Render missile
            if (((TileMissileContainer) node).getMissile() instanceof ICustomMissileRender)
            {
                GL11.glPushMatrix();
                GL11.glTranslated(xx + offset.x(), yy + offset.y(), zz + offset.z());
                //TODO rotate
                ((ICustomMissileRender) ((TileMissileContainer) node).getMissile()).renderMissileInWorld(0, 0, pass);
                GL11.glPopMatrix();
            }
        }
    }
}
