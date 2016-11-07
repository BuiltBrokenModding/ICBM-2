package com.builtbroken.icbm.content.launcher.launcher.large;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.launcher.TileAbstractLauncherPad;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
public class TileLargeLauncher extends TileAbstractLauncherPad
{
    public TileLargeLauncher()
    {
        super("largelauncher");
        this.hardness = 10f;
        this.resistance = 10f;
    }

    @Override
    public boolean canAcceptMissile(IMissile missile)
    {
        return super.canAcceptMissile(missile) && missile.getMissileSize() == MissileCasings.LARGE.ordinal();
    }

    @Override
    public Tile newTile()
    {
        return new TileLargeLauncher();
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:largeLauncher.container";
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        //TODO modified to fit missile
        return new Cube(-1, 0, -1, 2, 64, 2).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() - 0.5f, pos.yf() + 0.5f, pos.zf() + 2.5f);
        GL11.glScalef(1.5f, 1.5f, 1.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.STANDARD_MISSILE_MODEL.renderAll();
        GL11.glPopMatrix();
    }
}
