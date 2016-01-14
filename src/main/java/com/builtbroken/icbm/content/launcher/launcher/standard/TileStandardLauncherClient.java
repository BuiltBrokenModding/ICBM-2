package com.builtbroken.icbm.content.launcher.launcher.standard;

import com.builtbroken.icbm.content.Assets;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/13/2016.
 */
public class TileStandardLauncherClient extends TileStandardLauncher
{

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        //TODO modified to fit missile
        return new Cube(-1, 0, -1, 2, 8, 2).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos center, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        Pos pos = center;

        switch (ForgeDirection.getOrientation(getMetadata()))
        {
            case NORTH:
                pos = pos.add(0.7, -0.5, 1.9);
                break;
            case SOUTH:
                pos = pos.add(0.7, -0.5, 3.86);
                break;
            case EAST:
                pos = pos.add(1.7, -0.5, 2.86);
                break;
            case WEST:
                pos = pos.add(-0.3, -0.5, 2.86);
                break;
        }
        GL11.glTranslatef(pos.xf(), pos.yf(), pos.zf());
        GL11.glRotatef(45f, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.STANDARD_MISSILE_MODEL.renderAll();
        GL11.glPopMatrix();
    }
}
