package icbm.renders;
 
import icbm.ICBM;
import icbm.jiqi.TLeiDaTai;
import icbm.models.MLeiDa;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;
 
public class RenderRadarStation extends TileEntitySpecialRenderer
{
	private MLeiDa modelBase = new MLeiDa();
 
    public void renderAModelAt(TLeiDaTai tileEntity, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
        this.bindTextureByName(ICBM.TEXTURE_FILE_PATH+"Radar.png");
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        modelBase.renderRadar(tileEntity.xuanZhuan, 0.0625F);
        GL11.glPopMatrix();                  
    }
 
    @Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TLeiDaTai)tileentity, d, d1, d2, f);
    }
}