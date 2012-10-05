package icbm.renders;
 
import icbm.ICBM;
import icbm.jiqi.TLeiShePao;
import icbm.models.MLeiShePao;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;
 
public class RLeiShePao extends TileEntitySpecialRenderer
{
	private MLeiShePao modelBase = new MLeiShePao();
 
    public void renderAModelAt(TLeiShePao tileEntity, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
        //GL11.glScalef(1f, 1f, 1f);
        this.bindTextureByName(ICBM.TEXTURE_FILE_PATH+"LaserTurret.png");
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glRotatef(180F, 0F, 1F, 0F);
        modelBase.render(tileEntity.displayRotationYaw, tileEntity.displayRotationPitch, 0.0625F);
        GL11.glPopMatrix();                  
    }
 
    @Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TLeiShePao)tileentity, d, d1, d2, f);
    }
}