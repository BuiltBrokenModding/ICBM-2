package icbm.renders;

import icbm.ZhuYao;
import icbm.zhapin.EShouLiuDan;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Render;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RShouLiuDan extends Render
{
	private RenderBlocks renderBlocks = new RenderBlocks();

	/**
	 * The RNG used in RenderItem (for bobbing
	 * itemstacks on the ground)
	 */
	private Random random = new Random();
	public boolean field_77024_a = true;

	/**
	 * Defines the zLevel of rendering of item on
	 * GUI.
	 */
	public float zLevel = 0.0F;

	public RShouLiuDan()
	{
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	/**
	 * Renders the item
	 */
	public void doRenderItem(EShouLiuDan par1EntityItem, double par2, double par4, double par6, float par8, float par9)
	{
		this.random.setSeed(187L);
		ItemStack var10 = new ItemStack(ZhuYao.itShouLiuDan, 1, par1EntityItem.explosiveID);
		GL11.glPushMatrix();
		byte var13 = 1;

		GL11.glTranslatef((float) par2, (float) par4 + 0.5f, (float) par6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		int var16;
		float var19;
		float var20;
		float var24;

		if (var10.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.blocksList[var10.itemID].getRenderType()))
		{
			this.loadTexture(Block.blocksList[var10.itemID].getTextureFile());
			float var22 = 0.25F;
			var16 = Block.blocksList[var10.itemID].getRenderType();

			if (var16 == 1 || var16 == 19 || var16 == 12 || var16 == 2)
			{
				var22 = 0.5F;
			}

			GL11.glScalef(var22, var22, var22);

			for (int var23 = 0; var23 < var13; ++var23)
			{
				GL11.glPushMatrix();

				if (var23 > 0)
				{
					var24 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
					var19 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
					var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
					GL11.glTranslatef(var24, var19, var20);
				}

				var24 = 1.0F;
				this.renderBlocks.renderBlockAsItem(Block.blocksList[var10.itemID], var10.getItemDamage(), var24);
				GL11.glPopMatrix();
			}
		}
		else
		{
			int var15;
			float var17;

			if (var10.getItem().requiresMultipleRenderPasses())
			{
				GL11.glScalef(0.5F, 0.5F, 0.5F);

				this.loadTexture(Item.itemsList[var10.itemID].getTextureFile());

				for (var15 = 0; var15 <= var10.getItem().getRenderPasses(var10.getItemDamage()); ++var15)
				{
					this.random.setSeed(187L); // Fixes
												// Vanilla
												// bug
												// where
												// layers
												// would
												// not
												// render
												// aligns
												// properly.
					var16 = var10.getItem().getIconFromDamageForRenderPass(var10.getItemDamage(), var15);
					var17 = 1.0F;

					if (this.field_77024_a)
					{
						int var18 = Item.itemsList[var10.itemID].func_82790_a(var10, var15);
						var19 = (float) (var18 >> 16 & 255) / 255.0F;
						var20 = (float) (var18 >> 8 & 255) / 255.0F;
						float var21 = (float) (var18 & 255) / 255.0F;
						GL11.glColor4f(var19 * var17, var20 * var17, var21 * var17, 1.0F);
					}

					this.func_77020_a(var16, var13);
				}
			}
			else
			{
				GL11.glScalef(0.5F, 0.5F, 0.5F);

				var15 = var10.getIconIndex();

				this.loadTexture(var10.getItem().getTextureFile());

				if (this.field_77024_a)
				{
					var16 = Item.itemsList[var10.itemID].func_82790_a(var10, 0);
					var17 = (float) (var16 >> 16 & 255) / 255.0F;
					var24 = (float) (var16 >> 8 & 255) / 255.0F;
					var19 = (float) (var16 & 255) / 255.0F;
					var20 = 1.0F;
					GL11.glColor4f(var17 * var20, var24 * var20, var19 * var20, 1.0F);
				}

				this.func_77020_a(var15, var13);
			}
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private void func_77020_a(int par1, int par2)
	{
		Tessellator var3 = Tessellator.instance;
		float var4 = (float) (par1 % 16 * 16 + 0) / 256.0F;
		float var5 = (float) (par1 % 16 * 16 + 16) / 256.0F;
		float var6 = (float) (par1 / 16 * 16 + 0) / 256.0F;
		float var7 = (float) (par1 / 16 * 16 + 16) / 256.0F;
		float var8 = 1.0F;
		float var9 = 0.5F;
		float var10 = 0.25F;

		for (int var11 = 0; var11 < par2; ++var11)
		{
			GL11.glPushMatrix();

			if (var11 > 0)
			{
				float var12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
				float var13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
				float var14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
				GL11.glTranslatef(var12, var13, var14);
			}

			GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			var3.startDrawingQuads();
			var3.setNormal(0.0F, 1.0F, 0.0F);
			var3.addVertexWithUV((double) (0.0F - var9), (double) (0.0F - var10), 0.0D, (double) var4, (double) var7);
			var3.addVertexWithUV((double) (var8 - var9), (double) (0.0F - var10), 0.0D, (double) var5, (double) var7);
			var3.addVertexWithUV((double) (var8 - var9), (double) (1.0F - var10), 0.0D, (double) var5, (double) var6);
			var3.addVertexWithUV((double) (0.0F - var9), (double) (1.0F - var10), 0.0D, (double) var4, (double) var6);
			var3.draw();
			GL11.glPopMatrix();
		}
	}

	/**
	 * Renders the item's icon or block into the
	 * UI at the specified position.
	 */
	public void renderItemIntoGUI(FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
	{
		int var6 = par3ItemStack.itemID;
		int var7 = par3ItemStack.getItemDamage();
		int var8 = par3ItemStack.getIconIndex();
		int var10;
		float var12;
		float var13;
		float var16;

		if (par3ItemStack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.blocksList[par3ItemStack.itemID].getRenderType()))
		{
			Block var15 = Block.blocksList[var6];
			par2RenderEngine.bindTexture(par2RenderEngine.getTexture(var15.getTextureFile()));
			GL11.glPushMatrix();
			GL11.glTranslatef((float) (par4 - 2), (float) (par5 + 3), -3.0F + this.zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F);
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			var10 = Item.itemsList[var6].func_82790_a(par3ItemStack, 0);
			var16 = (float) (var10 >> 16 & 255) / 255.0F;
			var12 = (float) (var10 >> 8 & 255) / 255.0F;
			var13 = (float) (var10 & 255) / 255.0F;

			if (this.field_77024_a)
			{
				GL11.glColor4f(var16, var12, var13, 1.0F);
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			this.renderBlocks.useInventoryTint = this.field_77024_a;
			this.renderBlocks.renderBlockAsItem(var15, var7, 1.0F);
			this.renderBlocks.useInventoryTint = true;
			GL11.glPopMatrix();
		}
		else
		{
			int var9;

			if (Item.itemsList[var6].requiresMultipleRenderPasses())
			{
				GL11.glDisable(GL11.GL_LIGHTING);
				par2RenderEngine.bindTexture(par2RenderEngine.getTexture(Item.itemsList[var6].getTextureFile()));

				for (var9 = 0; var9 <= Item.itemsList[var6].getRenderPasses(var7); ++var9)
				{
					var10 = Item.itemsList[var6].getIconFromDamageForRenderPass(var7, var9);
					int var11 = Item.itemsList[var6].func_82790_a(par3ItemStack, var9);
					var12 = (float) (var11 >> 16 & 255) / 255.0F;
					var13 = (float) (var11 >> 8 & 255) / 255.0F;
					float var14 = (float) (var11 & 255) / 255.0F;

					if (this.field_77024_a)
					{
						GL11.glColor4f(var12, var13, var14, 1.0F);
					}

					this.renderTexturedQuad(par4, par5, var10 % 16 * 16, var10 / 16 * 16, 16, 16);
				}

				GL11.glEnable(GL11.GL_LIGHTING);
			}
			else if (var8 >= 0)
			{
				GL11.glDisable(GL11.GL_LIGHTING);

				par2RenderEngine.bindTexture(par2RenderEngine.getTexture(par3ItemStack.getItem().getTextureFile()));

				var9 = Item.itemsList[var6].func_82790_a(par3ItemStack, 0);
				float var17 = (float) (var9 >> 16 & 255) / 255.0F;
				var16 = (float) (var9 >> 8 & 255) / 255.0F;
				var12 = (float) (var9 & 255) / 255.0F;

				if (this.field_77024_a)
				{
					GL11.glColor4f(var17, var16, var12, 1.0F);
				}

				this.renderTexturedQuad(par4, par5, var8 % 16 * 16, var8 / 16 * 16, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public void func_82406_b(FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
	{
		if (par3ItemStack != null)
		{
			if (!ForgeHooksClient.renderInventoryItem(renderBlocks, par2RenderEngine, par3ItemStack, field_77024_a, zLevel, (float) par4, (float) par5))
			{
				this.renderItemIntoGUI(par1FontRenderer, par2RenderEngine, par3ItemStack, par4, par5);
			}

			if (par3ItemStack != null && par3ItemStack.hasEffect())
			{
				GL11.glDepthFunc(GL11.GL_GREATER);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDepthMask(false);
				par2RenderEngine.bindTexture(par2RenderEngine.getTexture("%blur%/misc/glint.png"));
				this.zLevel -= 50.0F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
				GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
				this.func_77018_a(par4 * 431278612 + par5 * 32178161, par4 - 2, par5 - 2, 20, 20);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				this.zLevel += 50.0F;
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}
		}
	}

	private void func_77018_a(int par1, int par2, int par3, int par4, int par5)
	{
		for (int var6 = 0; var6 < 2; ++var6)
		{
			if (var6 == 0)
			{
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			if (var6 == 1)
			{
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			float var7 = 0.00390625F;
			float var8 = 0.00390625F;
			float var9 = (float) (Minecraft.getSystemTime() % (long) (3000 + var6 * 1873)) / (3000.0F + (float) (var6 * 1873)) * 256.0F;
			float var10 = 0.0F;
			Tessellator var11 = Tessellator.instance;
			float var12 = 4.0F;

			if (var6 == 1)
			{
				var12 = -1.0F;
			}

			var11.startDrawingQuads();
			var11.addVertexWithUV((double) (par2 + 0), (double) (par3 + par5), (double) this.zLevel, (double) ((var9 + (float) par5 * var12) * var7), (double) ((var10 + (float) par5) * var8));
			var11.addVertexWithUV((double) (par2 + par4), (double) (par3 + par5), (double) this.zLevel, (double) ((var9 + (float) par4 + (float) par5 * var12) * var7), (double) ((var10 + (float) par5) * var8));
			var11.addVertexWithUV((double) (par2 + par4), (double) (par3 + 0), (double) this.zLevel, (double) ((var9 + (float) par4) * var7), (double) ((var10 + 0.0F) * var8));
			var11.addVertexWithUV((double) (par2 + 0), (double) (par3 + 0), (double) this.zLevel, (double) ((var9 + 0.0F) * var7), (double) ((var10 + 0.0F) * var8));
			var11.draw();
		}
	}

	/**
	 * Renders the item's overlay information.
	 * Examples being stack count or damage on top
	 * of the item's image at the specified
	 * position.
	 */
	public void renderItemOverlayIntoGUI(FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
	{
		if (par3ItemStack != null)
		{
			if (par3ItemStack.stackSize > 1)
			{
				String var6 = "" + par3ItemStack.stackSize;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				par1FontRenderer.drawStringWithShadow(var6, par4 + 19 - 2 - par1FontRenderer.getStringWidth(var6), par5 + 6 + 3, 16777215);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}

			if (par3ItemStack.isItemDamaged())
			{
				int var11 = (int) Math.round(13.0D - (double) par3ItemStack.getItemDamageForDisplay() * 13.0D / (double) par3ItemStack.getMaxDamage());
				int var7 = (int) Math.round(255.0D - (double) par3ItemStack.getItemDamageForDisplay() * 255.0D / (double) par3ItemStack.getMaxDamage());
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				Tessellator var8 = Tessellator.instance;
				int var9 = 255 - var7 << 16 | var7 << 8;
				int var10 = (255 - var7) / 4 << 16 | 16128;
				this.renderQuad(var8, par4 + 2, par5 + 13, 13, 2, 0);
				this.renderQuad(var8, par4 + 2, par5 + 13, 12, 1, var10);
				this.renderQuad(var8, par4 + 2, par5 + 13, var11, 1, var9);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	/**
	 * Adds a quad to the tesselator at the
	 * specified position with the set width and
	 * height and color. Args: tessellator, x, y,
	 * width, height, color
	 */
	private void renderQuad(Tessellator par1Tessellator, int par2, int par3, int par4, int par5, int par6)
	{
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setColorOpaque_I(par6);
		par1Tessellator.addVertex((double) (par2 + 0), (double) (par3 + 0), 0.0D);
		par1Tessellator.addVertex((double) (par2 + 0), (double) (par3 + par5), 0.0D);
		par1Tessellator.addVertex((double) (par2 + par4), (double) (par3 + par5), 0.0D);
		par1Tessellator.addVertex((double) (par2 + par4), (double) (par3 + 0), 0.0D);
		par1Tessellator.draw();
	}

	/**
	 * Adds a textured quad to the tesselator at
	 * the specified position with the specified
	 * texture coords, width and height. Args: x,
	 * y, u, v, width, height
	 */
	public void renderTexturedQuad(int par1, int par2, int par3, int par4, int par5, int par6)
	{
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + 0) * var7), (double) ((float) (par4 + par6) * var8));
		var9.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + par5) * var7), (double) ((float) (par4 + par6) * var8));
		var9.addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + par5) * var7), (double) ((float) (par4 + 0) * var8));
		var9.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + 0) * var7), (double) ((float) (par4 + 0) * var8));
		var9.draw();
	}

	/**
	 * Actually renders the given argument. This
	 * is a synthetic bridge method, always
	 * casting down its argument and then handing
	 * it off to a worker function which does the
	 * actual work. In all probabilty, the class
	 * Render is generic (Render<T extends Entity)
	 * and this method has signature public void
	 * doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is
	 * pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.doRenderItem((EShouLiuDan) par1Entity, par2, par4, par6, par8, par9);
	}
}
