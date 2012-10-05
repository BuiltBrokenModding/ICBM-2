package icbm.renders;

import icbm.ICBM;
import icbm.jiqi.TFaSheJia;
import icbm.models.MFaSheJia;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class RFaSheJia extends TileEntitySpecialRenderer
{
	private MFaSheJia model = new MFaSheJia();
	
	@Override
	public void renderTileEntityAt(TileEntity var1, double d, double d1, double d2, float var8)
	{
		TFaSheJia tileEntity = (TFaSheJia)var1;
		
		GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5F, (float)d1 + 1.25F, (float)d2 + 0.5F);
        GL11.glScalef(1f, 0.85f, 1f);
        
        String textureFile = ICBM.TEXTURE_FILE_PATH+"Launcher0.png";
        
        this.bindTextureByName(textureFile);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        
        if(tileEntity.getDirection() != ForgeDirection.NORTH && tileEntity.getDirection() != ForgeDirection.SOUTH)
        {
        	GL11.glRotatef(90F, 0.0F, 180F, 1.0F);
        }
        
        model.render(0.0625F);
        
        GL11.glPopMatrix();    
	}

}
