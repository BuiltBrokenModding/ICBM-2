package icbm.client.render;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RFake extends RenderEntity
{
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		if (this.renderManager == null)
		{
			this.setRenderManager(RenderManager.instance);
		}
	}
}
