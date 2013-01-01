package icbm.client.render;

import icbm.client.models.MShouFaSheQi;
import icbm.common.ZhuYao;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RItFaSheQi implements IItemRenderer
{
	public static final MShouFaSheQi MODEL = new MShouFaSheQi();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return item.itemID == ZhuYao.itFaSheQi.itemID;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return item.itemID == ZhuYao.itFaSheQi.itemID;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.TEXTURE_FILE_PATH + "RocketLauncher.png"));

		GL11.glTranslatef(0, 1.5f, 0);
		GL11.glRotatef(180, 0, 0, 1);

		if (type == ItemRenderType.INVENTORY)
		{
			GL11.glScalef(0.8f, 1f, 0.8f);
			GL11.glTranslatef(0, 0.3f, 0);
		}
		else if (type == ItemRenderType.EQUIPPED)
		{
			GL11.glTranslatef(0.5f, -1f, 0.5f);
			GL11.glRotatef(50, 0, 1, 0);
		}

		MODEL.render(0.0625F);

		GL11.glPopMatrix();
	}
}
