package icbm.explosion.render.tile;

import icbm.Reference;
import icbm.explosion.machines.TileCruiseLauncher;
import icbm.explosion.model.tiles.MXiaoFaSheQi;
import icbm.explosion.model.tiles.MXiaoFaSheQiJia;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCruiseLauncher extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "cruise_launcher.png");

    public static final MXiaoFaSheQi MODEL0 = new MXiaoFaSheQi();
    public static final MXiaoFaSheQiJia MODEL1 = new MXiaoFaSheQiJia();

    public void renderModelAt(TileCruiseLauncher tileEntity, double d, double d1, double d2, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
        this.bindTexture(TEXTURE_FILE);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        MODEL0.render(0.0625F);
        GL11.glRotatef(tileEntity.rotationYaw + 90, 0F, 1F, 0F);
        GL11.glRotatef(-tileEntity.rotationPitch, 1F, 0F, 0F);
        MODEL1.render(0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderModelAt((TileCruiseLauncher) tileentity, d, d1, d2, f);
    }
}