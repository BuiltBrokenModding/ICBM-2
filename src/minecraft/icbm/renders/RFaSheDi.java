package icbm.renders;
 
import icbm.api.ICBM;
import icbm.jiqi.TFaSheDi;
import icbm.models.MFaSheDi0;
import icbm.models.MFaSheDiRail0;
import icbm.models.MFaSheDi1;
import icbm.models.MFaSheDiRail1;
import icbm.models.MFaSheDi2;
import icbm.models.MFaSheDiRail2;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;
 
public class RFaSheDi extends TileEntitySpecialRenderer
{
	MFaSheDi0 modelBase0 = new MFaSheDi0();
	MFaSheDiRail0 modelRail0 = new MFaSheDiRail0();
	
	MFaSheDi1 modelBase1 = new MFaSheDi1();
	MFaSheDiRail1 modelRail1 = new MFaSheDiRail1();
	
	MFaSheDi2 modelBase2 = new MFaSheDi2();
	MFaSheDiRail2 modelRail2 = new MFaSheDiRail2();
	
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