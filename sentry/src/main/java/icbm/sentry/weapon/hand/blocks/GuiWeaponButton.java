package icbm.sentry.weapon.hand.blocks;

import org.lwjgl.opengl.GL11;

import icbm.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiWeaponButton extends GuiButton {

	public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_munition_printer.png");
	
	private ItemStack toRender;
	
	public GuiWeaponButton(int id, int x, int y, Item toShow) {
		super(id, x, y, 83, 18, null);
		this.toRender = new ItemStack(toShow);
	}
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {		
		this.drawSmallButton(par1Minecraft, this.xPosition, this.yPosition);
	}
	
	public void drawSmallButton(Minecraft mc, int x, int y) {
		mc.renderEngine.bindTexture(TEXTURE);
		this.drawTexturedModalRect(x, y, 0, 166, 83, 18);
	}

}
