package icbm.renders;

import net.minecraft.src.Entity;
import net.minecraft.src.RenderEntity;
import net.minecraft.src.RenderManager;

public class RFake extends RenderEntity
{
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
    	if(this.renderManager == null)
    	{
    		this.setRenderManager(RenderManager.instance);
    	}
    }
}
