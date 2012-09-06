package icbm;

import icbm.machines.TileEntityLauncherScreen;
import icbm.models.ModelLauncher0Screen;
import icbm.models.ModelLauncher1Screen;
import icbm.models.ModelLauncher2Screen;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

public class RenderLauncherScreen extends TileEntitySpecialRenderer
{
	ModelLauncher0Screen model0 = new ModelLauncher0Screen();
	ModelLauncher1Screen model1 = new ModelLauncher1Screen();
	ModelLauncher2Screen model2 = new ModelLauncher2Screen();
	
	@Override
	public void renderTileEntityAt(TileEntity var1, double d, double d1, double d2, float var8)
	{
		TileEntityLauncherScreen tileEntity = (TileEntityLauncherScreen)var1;
		
		GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5F, (float)d1 + 1.5F, (float)d2 + 0.5F);
        
        String textureFile = ICBM.TEXTURE_FILE_PATH+"Launcher"+tileEntity.getTier()+".png";
        
        this.bindTextureByName(textureFile);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        
        switch(tileEntity.getDirection().ordinal())
        {
	        case 2: GL11.glRotatef(180F, 0.0F, 180F, 1.0F); break;
	        case 4: GL11.glRotatef(90F, 0.0F, 180F, 1.0F); break;
	        case 5: GL11.glRotatef(-90F, 0.0F, 180F, 1.0F); break;
        }
        
        switch(tileEntity.getTier())
		{
			case 0: model0.render(0.0625F); break;
			case 1: model1.render(0.0625F); break;
			case 2: model2.render(0.0625F); break;
		}
        
        GL11.glPopMatrix();    
	}

}
