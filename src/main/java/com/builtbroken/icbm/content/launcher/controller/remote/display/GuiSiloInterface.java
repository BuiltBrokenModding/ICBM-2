package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileCommandSiloConnector;
import com.builtbroken.mc.api.map.radio.wireless.ConnectionStatus;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.imp.transform.region.Rectangle;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.EnumGuiIconSheet;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/23/2016.
 */
public class GuiSiloInterface extends GuiContainerBase
{
    /** Number of silos that can be listed on screen */
    public static final int SILO_ON_SCREEN = 6;

    EntityPlayer player;
    TileSiloInterfaceClient tileSiloInterface;

    int section = 0;
    int page = 0;
    int index = 0;

    List<ConnectionStatus> connectionStatuses = new ArrayList();

    /** Current page name for the connector being viewed */
    String connectorDisplayName;
    /** Current group name for the connector being viewed */
    String connectorGroupName;

    boolean refreshClick = true;

    public GuiSiloInterface(EntityPlayer player, TileSiloInterfaceClient tileSiloInterface)
    {
        super(new ContainerDummy(player, tileSiloInterface));
        this.player = player;
        this.tileSiloInterface = tileSiloInterface;
        this.baseTexture = SharedAssets.GUI__MC_EMPTY_FILE;
    }

    @Override
    public void initGui()
    {
        //TODO cache current state in client side tile, this way the page stays open (illusion)
        //TODO add login system to force users to auth with controllers, maybe add a security station for this?
        //TODO reduce request data and control viewed pages from server, restricts data & saves on packet size
        //TODO      also allows for several players to view the same page together

        //TODO add option to hide unwanted controller, allowing for a display to show one or two controllers at a time
        //TODO add an option to show/hide local controller, will need server side
        //TODO add an option to show/hide remote controller, will need server side
        super.initGui();

        //Clean up
        connectionStatuses.clear();
        tooltips.clear();
        connectorDisplayName = connectorGroupName = null;

        //Add buttons
        buttonList.add(GuiImageButton.newRefreshButton(0, guiLeft + 150, guiTop + 5));
        //TODO implement refresh icon
        //TODO while waiting on server data show large sync animation with text ("Waiting on server")

        //Not controller data, nothing to display
        if (tileSiloInterface.controllers != null && tileSiloInterface.controllerData != null && tileSiloInterface.controllers.length > 0)
        {
            if (tileSiloInterface.controllers.length > 1)
            {
                //TODO add tool tips for buttons
                //Controller section switch buttons
                buttonList.add(new GuiButton(5, guiLeft + 150, guiTop + 160, 20, 20, ">>"));
                buttonList.add(new GuiButton(6, guiLeft + 5, guiTop + 160, 20, 20, "<<"));
            }

            //Sanity check
            if (section >= tileSiloInterface.controllers.length)
            {
                section = Math.max(tileSiloInterface.controllers.length - 1, 0);
            }

            //Get page section
            if (section >= 0 && section < tileSiloInterface.controllers.length)
            {
                //TODO add tool tips for buttons
                //Page switch buttons
                int maxPages = maxPageCount();
                buttonList.add(new GuiButton2(1, guiLeft + 150, guiTop + 140, 20, 20, ">").setEnabled(maxPages >= 1));
                buttonList.add(new GuiButton2(2, guiLeft + 5, guiTop + 140, 20, 20, "<").setEnabled(maxPages >= 1));

                //Get position data for the section
                Pos[] positions = tileSiloInterface.controllerData[section];
                //No position data or cached info, nothing to display
                if (positions != null && tileSiloInterface.clientSiloDataCache != null && tileSiloInterface.clientSiloDataCache.size() > 0 && positions.length > 0)
                {
                    //Santiy check
                    if (page >= positions.length)
                    {
                        page = positions.length - 1;
                    }

                    //Get position -> get silo data
                    Pos pos = positions[page];
                    List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(pos);

                    //Check if tile exists, if yes then display name and group
                    TileEntity tile = pos.getTileEntity(player.worldObj);
                    if (tile instanceof TileCommandSiloConnector)
                    {
                        connectorDisplayName = ((TileCommandSiloConnector) tile).getConnectorDisplayName();
                        connectorGroupName = ((TileCommandSiloConnector) tile).getConnectorGroupName();
                    }

                    //No silos, nothing to display
                    if (silos != null && silos.size() > 0)
                    {
                        //Sanity check
                        if (index >= silos.size())
                        {
                            index = 0;
                        }
                        //If more than x silo, add buttons to scroll
                        if (silos.size() > SILO_ON_SCREEN)
                        {
                            //TODO add tool tips for buttons
                            //Index switch buttons
                            buttonList.add(new GuiButton(3, guiLeft + 150, guiTop + 30, 20, 20, "-"));
                            buttonList.add(new GuiButton(4, guiLeft + 150, guiTop + 50, 20, 20, "+"));
                            //TODO add actual scroll bar
                            //TODO add scroll wheel support
                            //TODO add arrow key support
                        }

                        //Build display list
                        int row = 0;
                        for (int i = index; i < silos.size() && i < index + SILO_ON_SCREEN; i++)
                        {
                            ISiloConnectionData data = silos.get(i);
                            GuiButton2 button = new GuiButton2(10 + i, guiLeft + 36, guiTop + 10 + (row * 21), 80, 20, "Silo[" + i + "]");
                            if (data != null)
                            {
                                if (data.getSiloName() != null && !data.getSiloName().isEmpty())
                                {
                                    String siloName = data.getSiloName();
                                    if (siloName.length() > 8)
                                    {
                                        siloName = siloName.substring(0, 8);
                                    }
                                    button.displayString = "Silo[" + siloName + "]";
                                }
                                connectionStatuses.add(data.getSiloStatus());
                            }
                            else
                            {
                                connectionStatuses.add(ConnectionStatus.NO_CONNECTION);
                            }
                            if (connectionStatuses.get(connectionStatuses.size() - 1) == ConnectionStatus.NO_CONNECTION)
                            {
                                button.disable();
                            }
                            buttonList.add(new GuiButton2(30 + i, guiLeft + 117, guiTop + 10 + (row * 21), 30, 20, "Fire").setEnabled(connectionStatuses.get(connectionStatuses.size() - 1) == ConnectionStatus.ONLINE));
                            buttonList.add(button);
                            row++;
                        }
                    }
                    else
                    {
                        //TODO show error saying no connections
                    }

                    //Create tool tips for connection icons
                    for (int i = 0; i < connectionStatuses.size(); i++)
                    {
                        tooltips.put(new Rectangle(10, 10 + (i * 21), 28, 28 + (i * 21)), connectionStatuses.get(i).toString());
                    }
                }
            }
        }
        //No data to show, so reset values
        else
        {
            section = 0;
            page = 0;
            index = 0;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (tileSiloInterface.controllers != null && tileSiloInterface.controllerData != null && tileSiloInterface.controllers.length > 0)
        {
            if (connectorDisplayName != null && !connectorDisplayName.isEmpty())
            {
                drawString("Name: " + connectorDisplayName, 35, 143, java.awt.Color.black);
                if (connectorGroupName != null && !connectorGroupName.isEmpty())
                {
                    drawString("GroupID: " + connectorGroupName, 35, 153, java.awt.Color.black);
                }
            }
            else
            {
                drawString("Page " + (page + 1), 73, 148);
            }
        }
        else
        {
            drawString("No data Synced", 4, 5, Color.black);
            drawString("Click the refresh button -->", 4, 15);

            if(refreshClick)
            {
                drawString("Nothing Happens", 6, 30, Color.black);
                drawString(" If you click and nothing happens", 6, 40);
                drawString(" Ensure everything is connected", 6, 50);

                drawString("How to connect", 6, 65, Color.black);
                drawString(" 1. Use data chip to link", 6, 75);
                drawString(" 2. Shift+right click controller", 6, 85);
                drawString(" 3. Right click display", 6, 95);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        for (int i = 0; i < connectionStatuses.size(); i++)
        {
            switch (connectionStatuses.get(i))
            {
                case ONLINE:
                    EnumGuiIconSheet.STATUS_ON.draw(this, 10 + guiLeft, 10 + (i * 21) + guiTop);
                    break;
                case OFFLINE:
                    EnumGuiIconSheet.STATUS_OFF.draw(this, 10 + guiLeft, 10 + (i * 21) + guiTop);
                    break;
                case NO_CONNECTION:
                    EnumGuiIconSheet.STATUS_CONNECTION_LOST.draw(this, 10 + guiLeft, 10 + (i * 21) + guiTop);
                    break;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            tileSiloInterface.requestSiloData();
            refreshClick = true;
        }
        else if (button.id == 1) //Next
        {
            if (page < maxPageCount())
            {
                page++;
            }
            else
            {
                page = 0;
            }
            initGui(); //refresh
        }
        else if (button.id == 2) //Prev
        {
            if (page > 0)
            {
                page--;
            }
            else
            {
                page = maxPageCount();
            }
            initGui(); //Refresh
        }
        else if (button.id == 3 || button.id == 4)
        {
            if (page >= 0 && page < maxPageCount())
            {
                if (button.id == 4) //Next
                {
                    if (index < maxIndex())
                    {
                        index++;
                    }
                    else
                    {
                        index = 0;
                    }
                    initGui(); //refresh
                }
                else if (button.id == 3) //Prev
                {
                    if (index > 0)
                    {
                        index--;
                    }
                    else
                    {
                        index = maxIndex();
                    }
                }
            }
            else
            {
                page = 0;
            }
            initGui(); //Refresh
        }
        else if (button.id == 5 || button.id == 6)
        {
            if (tileSiloInterface.controllers != null && section >= 0 && section < tileSiloInterface.controllers.length)
            {
                if (button.id == 6) //Next
                {
                    if (section < (tileSiloInterface.controllers.length - 1))
                    {
                        section++;
                    }
                    else
                    {
                        section = 0;
                    }
                }
                else if (button.id == 5) //Prev
                {
                    if (section > 0)
                    {
                        section--;
                    }
                    else
                    {
                        section = tileSiloInterface.controllers.length - 1;
                    }
                }
                page = 0;
                index = 0;
            }
            else
            {
                section = 0;
            }
            initGui(); //Refresh
        }
        else if (button.id >= 30)
        {
            int siloIndex = button.id - 30;
            //This does our page limit check for us, on top of the index check
            if (siloIndex >= 0 && siloIndex <= maxIndex())
            {
                Pos pos = tileSiloInterface.controllerData[section][page];
                List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(pos);
                tileSiloInterface.fireSilo(pos, silos.get(siloIndex), player);
            }
        }
        else if (button.id >= 10)
        {
            int siloIndex = button.id - 10;

            //This does our page limit check for us, on top of the index check
            if (siloIndex >= 0 && siloIndex <= maxIndex())
            {
                Pos pos = tileSiloInterface.controllerData[section][page];
                List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(pos);
                tileSiloInterface.openSiloGui(pos, silos.get(siloIndex), player);
            }
        }
    }

    protected final int maxPageCount()
    {
        return tileSiloInterface.controllerData != null && section >= 0 && section < tileSiloInterface.controllerData.length && tileSiloInterface.controllerData[section] != null ? Math.max(tileSiloInterface.controllerData[section].length - 1, 0) : 0;
    }

    protected final int maxIndex()
    {
        if (page >= 0 && page <= maxPageCount())
        {
            List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(tileSiloInterface.controllerData[section][page]);
            return silos == null ? 0 : Math.max(silos.size() - 1, 0);
        }
        return 0;
    }
}
