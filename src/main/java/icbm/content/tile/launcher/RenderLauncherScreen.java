package icbm.content.tile.launcher;

import icbm.Reference;
import icbm.content.render.models.ModelTier1Screen;
import icbm.content.render.models.ModelTier2Screen;
import icbm.content.render.models.ModelTier3Screen;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLauncherScreen extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE_0 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_0.png");
    public static final ResourceLocation TEXTURE_FILE_1 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_1.png");
    public static final ResourceLocation TEXTURE_FILE_2 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_2.png");

    public static final ModelTier1Screen model0 = new ModelTier1Screen();
    public static final ModelTier2Screen model1 = new ModelTier2Screen();
    public static final ModelTier3Screen model2 = new ModelTier3Screen();

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8)
    {
        TileLauncherScreen tileEntity = (TileLauncherScreen) var1;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        switch (tileEntity.getDirection().ordinal())
        {
            case 2:
                GL11.glRotatef(180F, 0.0F, 180F, 1.0F);
                break;
            case 4:
                GL11.glRotatef(90F, 0.0F, 180F, 1.0F);
                break;
            case 5:
                GL11.glRotatef(-90F, 0.0F, 180F, 1.0F);
                break;
        }

        switch (tileEntity.getTier())
        {
            case 0:
                this.bindTexture(TEXTURE_FILE_0);
                model0.render(0.0625F);
                break;
            case 1:
                this.bindTexture(TEXTURE_FILE_1);
                model1.render(0.0625F);
                break;
            case 2:
                this.bindTexture(TEXTURE_FILE_2);
                model2.render(0.0625F);
                break;
        }

        GL11.glPopMatrix();
    }

}
