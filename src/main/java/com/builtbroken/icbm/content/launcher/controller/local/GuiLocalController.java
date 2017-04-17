package com.builtbroken.icbm.content.launcher.controller.local;

import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.launcher.controller.LauncherData;
import com.builtbroken.icbm.content.launcher.silo.TileSmallSilo;
import com.builtbroken.icbm.content.launcher.silo.TileStandardSilo;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Main GUI for the small missile silo controller
 * Created by robert on 2/20/2015.
 */
public class GuiLocalController extends GuiContainerBase
{
    protected TileLocalController controller;

    GuiButton[] buttons;

    protected GuiTextField x_field;
    protected GuiTextField y_field;
    protected GuiTextField z_field;
    protected GuiTextField name_field;
    protected String errorString = "";

    boolean editMode = false;
    int editMissile = 0;
    long lastClickTime = 0;

    private int ticks = 0;
    private static int updateGuiTicks = 100;


    public GuiLocalController(TileLocalController launcher, EntityPlayer player)
    {
        super(new ContainerDummy(player, launcher));
        this.controller = launcher;
        this.baseTexture = SharedAssets.GUI__MC_EMPTY_FILE;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = this.guiLeft + 10;
        int y = this.guiTop + 20;

        if (!editMode)
        {
            if (controller.launcherData != null)
            {
                String launcherName = LanguageUtility.getLocalName("gui.icbm:controller.launcher");
                String siloName = LanguageUtility.getLocalName("gui.icbm:controller.silo");
                for (int i = 0; i < controller.launcherData.size(); i++)
                {
                    buttons = new GuiButton[controller.launcherData.size() * 2];


                    String buttonName = launcherName;
                    String name = "" + i;
                    ITileNode tile = controller.launcherData.get(i).location.getTileNode(controller.world());
                    if (tile instanceof TileAbstractLauncher)
                    {
                        if (tile instanceof TileSmallSilo || tile instanceof TileStandardSilo)
                        {
                            buttonName = siloName;
                        }
                        String n = ((TileAbstractLauncher) tile).getCustomName();
                        if (n != null && !n.isEmpty() && !n.equals("null")) //Not sure why null value shows up as a string in this case
                        {
                            if (n.length() > 12)
                            {
                                n = n.substring(0, 11) + "..";
                            }
                            name = n;
                        }
                    }
                    buttonName += "[" + name + "]";
                    //Launcher edit button

                    buttons[i] = new GuiButton(i, x, y, 120, 20, buttonName);
                    this.buttonList.add(buttons[i]);

                    //Fire launcher button
                    buttons[i + controller.launcherData.size()] = new GuiButton(i + controller.launcherData.size(), x + 125, y, 30, 20, LanguageUtility.getLocalName("gui.icbm:controller.fire"));
                    if (controller.launcherData.get(i).missile == null)
                    {
                        buttons[i + controller.launcherData.size()].enabled = false;
                    }
                    this.buttonList.add(buttons[i + controller.launcherData.size()]);
                    y += 22;
                }
            }
        }
        else
        {
            this.buttonList.add(new GuiButton(0, x + 65, y + 85, 30, 20, LanguageUtility.getLocalName("gui.icbm:controller.back")));
            this.buttonList.add(new GuiButton(1, x + 100, y + 85, 50, 20, LanguageUtility.getLocalName("gui.icbm:controller.update")));

            x = guiLeft + 10;
            y = guiTop + 42;

            ITileNode tile = controller.launcherData.get(editMissile).location.getTileNode(controller.world());

            if (tile instanceof TileAbstractLauncher && ((TileAbstractLauncher) tile).target != null)
            {
                this.x_field = this.newField(x, y, 40, 20, "" + ((TileAbstractLauncher) tile).target.xi());
                this.y_field = this.newField(x + 45, y, 40, 20, "" + ((TileAbstractLauncher) tile).target.yi());
                this.z_field = this.newField(x + 90, y, 40, 20, "" + ((TileAbstractLauncher) tile).target.zi());
                this.name_field = this.newField(x, y + 38, 90, 20, ((TileAbstractLauncher) tile).getCustomName() != null ? ((TileAbstractLauncher) tile).getCustomName() : "");
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (ticks++ >= updateGuiTicks)
        {
            reloadData();
        }
        String name = LanguageUtility.getLocalName(controller.getInventoryName());
        if (editMode)
        {
            if(controller != null && controller.launcherData != null && controller.launcherData.size() > editMissile)
            {
                LauncherData data = controller.launcherData.get(editMissile);
                if(data.location != null)
                {
                    Block block = data.location.getBlock(controller.world());
                    if(block != null)
                    {
                        String localization = block.getLocalizedName();
                        if (localization != null && !localization.contains("tile."))
                        {
                            name = localization;
                        }
                    }
                }
            }
        }
        drawStringCentered(name, 85, 10);


        if (!editMode && (controller.launcherData == null || controller.launcherData.size() == 0))
        {
            drawStringCentered(LanguageUtility.getLocal("gui.icbm:controller.links.none"), 85, 40);
            drawStringCentered(LanguageUtility.getLocal("gui.icbm:controller.links.none.hint"), 85, 50);
        }
        else if (editMode)
        {
            drawString(LanguageUtility.getLocalName("gui.icbm:controller.target"), 10, 30);
            drawString(LanguageUtility.getLocalName("gui.icbm:controller.launcherName"), 10, 70);
        }
    }

    public void reloadData()
    {
        if (!editMode)
        {
            initGui();
        }
        else if (this.x_field != null && this.y_field != null && this.z_field != null && this.x_field.isFocused() && this.y_field.isFocused() && this.z_field.isFocused())
        {
            ITileNode tile = controller.launcherData.get(editMissile).location.getTileNode(controller.world());

            if (tile instanceof TileAbstractLauncher && ((TileAbstractLauncher) tile).target != null)
            {
                this.x_field.setText("" + ((TileAbstractLauncher) tile).target.xi());
                this.y_field.setText("" + ((TileAbstractLauncher) tile).target.yi());
                this.z_field.setText("" + ((TileAbstractLauncher) tile).target.zi());
                this.name_field.setText("" + ((TileAbstractLauncher) tile).getCustomName());
            }
        }
    }


    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        //Prevents double click when GUI is reloaded
        if (Minecraft.getSystemTime() - lastClickTime < 2)
        {
            return;
        }
        this.errorString = "";

        if (!editMode)
        {
            if (button.id >= 0 && button.id < buttons.length)
            {
                //Edit launcher button
                if (button.id < controller.launcherData.size())
                {
                    editMissile = button.id;
                    editMode = true;
                    initGui();
                }
                //Fire launcher
                else
                {
                    controller.fireLauncher(button.id - controller.launcherData.size(), Minecraft.getMinecraft().thePlayer);
                }
            }
        }
        else
        {
            if (button.id == 0)
            {
                editMissile = -1;
                editMode = false;
                initGui();
            }
            else if (button.id == 1)
            {
                ITileNode tile = controller.launcherData.get(editMissile).location.getTileNode(controller.world());
                if (tile instanceof TileAbstractLauncher)
                {
                    try
                    {
                        ((TileAbstractLauncher) tile).setTarget(new Pos(Integer.parseInt(x_field.getText()), Integer.parseInt(y_field.getText()), Integer.parseInt(z_field.getText())));
                    }
                    catch (NumberFormatException e)
                    {
                        errorString = "gui.icbm:controller.invalid.input";
                    }
                    ((TileAbstractLauncher) tile).setCustomName("" + name_field.getText());
                }
            }
        }

        lastClickTime = Minecraft.getSystemTime();
    }
}
