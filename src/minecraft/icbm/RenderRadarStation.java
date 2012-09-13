package icbm;
 
import icbm.jiqi.TLeiDa;
import icbm.models.ModelRadar;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;
 
public class RenderRadarStation extends TileEntitySpecialRenderer
{
	private ModelRadar modelBase = new ModelRadar();
 
    public void renderAModelAt(TLeiDa tileEntity, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
        this.bindTextureByName(ICBM.TEXTURE_FILE_PATH+"Radar.png");
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        modelBase.renderRadar(tileEntity.radarRotationYaw, 0.0625F);
        GL11.glPopMatrix();                  
    }
 
    @Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TLeiDa)tileentity, d, d1, d2, f);
    }
}