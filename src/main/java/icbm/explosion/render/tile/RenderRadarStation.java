package icbm.explosion.render.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.Reference;
import icbm.explosion.machines.TileRadarStation;
import icbm.explosion.model.tiles.ModelRadarStation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderRadarStation extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "radar.png");
    public static final ResourceLocation TEXTURE_FILE_OFF = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "radar_off.png");

    public static final ModelRadarStation MODEL = new ModelRadarStation();

    public void renderAModelAt(TileRadarStation tileEntity, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        if (tileEntity.energy.checkExtract())
        {
            this.bindTexture(TEXTURE_FILE);
        }
        else
        {
            this.bindTexture(TEXTURE_FILE_OFF);
        }

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

        MODEL.render(0.0625F, 0f, tileEntity.rotation);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TileRadarStation) tileentity, d, d1, d2, f);
    }
}