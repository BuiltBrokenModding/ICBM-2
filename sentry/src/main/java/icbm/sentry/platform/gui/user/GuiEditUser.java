package icbm.sentry.platform.gui.user;

import icbm.Reference;
import icbm.core.prefab.render.GuiICBMContainer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import calclavia.lib.access.AccessUser;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;

/** Gui for editing the group and access of the user. Should should the user's name, then the group
 * bellow that with a scroll buttons to cycle threw the groups. Bellow that it should show the
 * options that are locked in for the group and the options that can be added to the user alone.
 * Return button should be added near the bottom to go back to the user access page. A delete button
 * should also be included to remove the user. The user should not be able to edit there own
 * settings.
 * 
 * @author DarkGuardsman */
public class GuiEditUser extends GuiICBMContainer
{
    private GuiUserAccess return_gui;
    private AccessUser user;

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_grey.png");

    public GuiEditUser(GuiUserAccess return_gui, AccessUser user)
    {
        super(return_gui.inventorySlots);
        this.return_gui = return_gui;
        this.user = user;

    }

    @Override
    public void initGui()
    {
        super.initGui();
        //TODO implement back button to return user to calling gui
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        //FMLCommonHandler.instance().showGuiScreen(return_gui);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRenderer.drawString("\u00a77" + user.getName(), (int) (this.containerWidth / 2 - 7 * 2.5), 4, 4210752);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int containerWidth = (this.width - this.containerWidth) / 2;
        int containerHeight = (this.height - this.containerHeight) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.containerWidth, this.containerHeight);
    }

}
