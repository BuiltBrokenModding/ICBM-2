package icbm.zhapin.render;

import icbm.core.ZhuYao;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.daodan.DaoDan;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class RItDaoDan implements IItemRenderer
{

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return this.shouldUseRenderHelper(type, item, null);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return item.itemID == ZhuYaoZhaPin.itDaoDan.itemID || (item.itemID == ZhuYaoZhaPin.itTeBieDaoDan.itemID && item.getItemDamage() > 0);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		if (this.shouldUseRenderHelper(type, item, null))
		{
			float scale = 0.4f;
			float right = 0.15f;

			if (ZhaPin.list[item.getItemDamage()].getTier() == 2 || item.itemID == ZhuYaoZhaPin.itTeBieDaoDan.itemID)
			{
				scale = 0.3f;
			}
			else if (ZhaPin.list[item.getItemDamage()].getTier() == 3)
			{
				scale = 0.25f;
				right = 0.4f;
			}

			if (type == ItemRenderType.INVENTORY)
			{
				GL11.glTranslatef(right, 0f, 0f);
			}

			if (type == ItemRenderType.EQUIPPED)
			{
				GL11.glTranslatef(-0.15f, 1.2f, 0.5f);
				GL11.glRotatef(180, 0, 0, 1f);
			}
			else
			{
				GL11.glRotatef(-90, 0, 0, 1f);
			}

			GL11.glScalef(scale, scale, scale);

			if (item.itemID == ZhuYaoZhaPin.itTeBieDaoDan.itemID)
			{
				if (item.getItemDamage() - 1 < RDaoDan.SPECIAL_MODELS.length)
				{
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.MODEL_PATH + "missile_" + DaoDan.list[item.getItemDamage() + 100].getUnlocalizedName() + ".png"));
					RDaoDan.SPECIAL_MODELS[item.getItemDamage() - 1].render(0.0625F);
				}
			}
			else
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYao.MODEL_PATH + "missile_" + ZhaPin.list[item.getItemDamage()].getUnlocalizedName() + ".png"));
				RDaoDan.MODELS[item.getItemDamage()].render(0.0625F);
			}
		}
	}
}
