package icbm.zhapin.render;

import icbm.core.ZhuYaoBase;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.daodan.DaoDan;
import icbm.zhapin.daodan.ItDaoDan;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
		return item.getItem() instanceof ItDaoDan;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		if (this.shouldUseRenderHelper(type, item, null))
		{
			float scale = 0.7f;
			float right = 0f;

			if (type == ItemRenderType.INVENTORY)
			{
				scale = 0.4f;
				right = 0.15f;

				if (ZhaPin.list[item.getItemDamage()].getTier() == 2 || item.itemID == ZhuYaoZhaPin.itTeBieDaoDan.itemID)
				{
					scale = scale / 1.5f;
				}
				else if (ZhaPin.list[item.getItemDamage()].getTier() == 3)
				{
					scale = scale / 1.7f;
					right = 0.5f;
				}
				else if (ZhaPin.list[item.getItemDamage()].getTier() == 4)
				{
					scale = scale / 1.4f;
					right = 0.2f;
				}

				GL11.glTranslatef(right, 0f, 0f);
			}

			if (type == ItemRenderType.EQUIPPED)
			{
				GL11.glTranslatef(1.15f, 1f, 0.5f);
				GL11.glRotatef(180, 0, 0, 1f);
			}
			else
			{
				GL11.glRotatef(-90, 0, 0, 1f);
			}

			if (type == ItemRenderType.ENTITY)
			{
				scale = scale / 1.5f;
			}

			GL11.glScalef(scale, scale, scale);

			if (item.itemID == ZhuYaoZhaPin.itTeBieDaoDan.itemID)
			{
				if (item.getItemDamage() < RDaoDan.SPECIAL_MODELS.length)
				{
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYaoBase.MODEL_PATH + "missile_" + DaoDan.list[item.getItemDamage() + 100].getUnlocalizedName() + ".png"));
					RDaoDan.SPECIAL_MODELS[item.getItemDamage()].render(0.0625F);
				}
			}
			else
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(ZhuYaoBase.MODEL_PATH + "missile_" + ZhaPin.list[item.getItemDamage()].getUnlocalizedName() + ".png"));
				RDaoDan.MODELS[item.getItemDamage()].render(0.0625F);
			}
		}
	}
}
