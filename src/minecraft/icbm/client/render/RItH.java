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
public class RItH implements IItemRenderer
{
	public static final MShouFaSheQi MODEL = new MShouFaSheQi();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return item.itemID == ZhuYao.itFaSheQi.shiftedIndex;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return item.itemID == ZhuYao.itFaSheQi.shiftedIndex;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.TEXTURE_FILE_PATH + "RocketLauncher.png"));

		if (type == ItemRenderType.INVENTORY)
		{
			GL11.glScalef(0.8f, 1f, 0.8f);
			GL11.glTranslatef(0, -1.2f, 0);
		}

		MODEL.render(0.0625F);

		GL11.glPopMatrix();
	}
}
