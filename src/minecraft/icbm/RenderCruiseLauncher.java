package icbm;
 
import icbm.machines.TileEntityCruiseLauncher;
import icbm.models.ModelCruiseLauncherBase;
import icbm.models.ModelCruiseLauncherRail;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;
 
public class RenderCruiseLauncher extends TileEntitySpecialRenderer
{
	private ModelCruiseLauncherBase modelBase = new ModelCruiseLauncherBase();
	private ModelCruiseLauncherRail modelBase2 = new ModelCruiseLauncherRail();
 
    public void renderAModelAt(TileEntityCruiseLauncher tileEntity, double d, double d1, double d2, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5F, (float)d1 + 1.5F, (float)d2 + 0.5F);
        this.bindTextureByName(ICBM.TEXTURE_FILE_PATH+"CruiseLauncher.png");
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        modelBase.render(0.0625F);
        GL11.glRotatef(tileEntity.rotationYaw+90, 0F, 1F, 0F);
        GL11.glRotatef(-tileEntity.rotationPitch, 1F, 0F, 0F);
        modelBase2.render(0.0625F);
        GL11.glPopMatrix();                  
    }
 
    @Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
        renderAModelAt((TileEntityCruiseLauncher)tileentity, d, d1, d2, f);
    }
}