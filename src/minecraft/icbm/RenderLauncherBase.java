package icbm;
 
import icbm.jiqi.TFaSheDi;
import icbm.models.ModelLauncher0Base;
import icbm.models.ModelLauncher0Rail;
import icbm.models.ModelLauncher1Base;
import icbm.models.ModelLauncher1Rail;
import icbm.models.ModelLauncher2Base;
import icbm.models.ModelLauncher2Rails;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;
 
public class RenderLauncherBase extends TileEntitySpecialRenderer
{
	ModelLauncher0Base modelBase0 = new ModelLauncher0Base();
	ModelLauncher0Rail modelRail0 = new ModelLauncher0Rail();
	
	ModelLauncher1Base modelBase1 = new ModelLauncher1Base();
	ModelLauncher1Rail modelRail1 = new ModelLauncher1Rail();
	
	ModelLauncher2Base modelBase2 = new ModelLauncher2Base();
	ModelLauncher2Rails modelRail2 = new ModelLauncher2Rails();
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f)
    {
    	TFaSheDi tileEntity = (TFaSheDi)tileentity;
    	
    	GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5F, (float)d1 + 1.5F, (float)d2 + 0.5F);
        
        String textureFile = ICBM.TEXTURE_FILE_PATH+"Launcher"+tileEntity.getTier()+".png";
        
        this.bindTextureByName(textureFile);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        
        if(tileEntity.getDirection() != ForgeDirection.NORTH && tileEntity.getDirection() != ForgeDirection.SOUTH)
        {
        	GL11.glRotatef(90F, 0F, 180F, 1.0F);
        }
        
        //The missile launcher screen
        if(tileEntity.getTier() == 0)
        {
        	modelBase0.render(0.0625F);
        	modelRail0.render(0.0625F);
        }
        else if(tileEntity.getTier() == 1)
        {
        	modelBase1.render(0.0625F);
        	modelRail1.render(0.0625F);
        	GL11.glRotatef(180F, 0F, 180F, 1.0F);
        	modelRail1.render(0.0625F);
        }
        else if(tileEntity.getTier() == 2)
        {
        	modelBase2.render(0.0625F);
        	modelRail2.render(0.0625F);
    		GL11.glRotatef(180F, 0F, 180F, 1.0F);
    		modelRail2.render(0.0625F);
        }

        GL11.glPopMatrix();     
    }
}