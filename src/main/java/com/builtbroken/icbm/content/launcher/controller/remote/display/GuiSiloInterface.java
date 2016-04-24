package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.content.launcher.controller.remote.connector.TileCommandSiloConnector;
import com.builtbroken.mc.api.map.radio.wireless.ConnectionStatus;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.transform.region.Rectangle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.EnumGuiIconSheet;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

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
    TileSiloInterface tileSiloInterface;

    int page = 0;
    int index = 0;

    List<ConnectionStatus> connectionStatuses = new ArrayList();

    /** Current page name for the connector being viewed */
    String connectorDisplayName;
    /** Current group name for the connector being viewed */
    String connectorGroupName;

    public GuiSiloInterface(EntityPlayer player, TileSiloInterface tileSiloInterface)
    {
        super(new ContainerDummy(player, tileSiloInterface));
        this.player = player;
        this.tileSiloInterface = tileSiloInterface;
        this.baseTexture = References.GUI__MC_EMPTY_FILE;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButton(0, guiLeft + 150, guiTop + 5, 20, 20, "R")); //TODO implement refresh icon
        buttonList.add(new GuiButton(1, guiLeft + 150, guiTop + 140, 20, 20, ">"));
        buttonList.add(new GuiButton(2, guiLeft + 5, guiTop + 140, 20, 20, "<"));

        connectionStatuses.clear();
        if (tileSiloInterface.clientSiloDataCache != null && tileSiloInterface.clientSiloDataCache.size() > 0 && tileSiloInterface.siloConnectorPositionCache.size() > 0)
        {
            if (page >= tileSiloInterface.siloConnectorPositionCache.size())
            {
                page = tileSiloInterface.siloConnectorPositionCache.size() - 1;
            }
            Pos pos = tileSiloInterface.siloConnectorPositionCache.get(page);
            List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(pos);

            TileEntity tile = pos.getTileEntity(player.worldObj);
            if (tile instanceof TileCommandSiloConnector)
            {
                connectorDisplayName = ((TileCommandSiloConnector) tile).getConnectorDisplayName();
                connectorGroupName = ((TileCommandSiloConnector) tile).getConnectorGroupName();
            }
            if (silos != null && silos.size() > 0)
            {
                if (index >= silos.size())
                {
                    index = 0;
                }
                if (silos.size() > SILO_ON_SCREEN)
                {
                    buttonList.add(new GuiButton(3, guiLeft + 150, guiTop + 30, 20, 20, "-"));
                    buttonList.add(new GuiButton(4, guiLeft + 150, guiTop + 50, 20, 20, "+"));
                }
                int row = 0;
                for (int i = index; i < silos.size() && i < index + SILO_ON_SCREEN; i++)
                {
                    ISiloConnectionData data = silos.get(i);
                    GuiButton2 button = new GuiButton2(10 + i, guiLeft + 36, guiTop + 10 + (row * 21), 80, 20, "Silo[" + i + "]");
                    if (data != null)
                    {
                        if (data.getSiloName() != null && !data.getSiloName().isEmpty())
                        {
                            button.displayString = "Silo[" + data.getSiloName() + "]"; //TODO trim name length
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
                    buttonList.add(new GuiButton2(30 + i, guiLeft + 117, guiTop + 10 + (row * 21), 30, 20, "Fire"));
                    buttonList.add(button);
                    row++;
                }

            }
        }
        tooltips.clear();
        for (int i = 0; i < connectionStatuses.size(); i++)
        {
            tooltips.put(new Rectangle(10, 10 + (i * 21), 28, 28 + (i * 21)), connectionStatuses.get(i).toString());
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
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
        else if (button.id >= 30)
        {
            if (page >= 0 && page < tileSiloInterface.siloConnectorPositionCache.size())
            {
                int siloIndex = button.id - 30;
                List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(tileSiloInterface.siloConnectorPositionCache.get(page));
                if (siloIndex >= 0 && siloIndex < silos.size())
                {
                    Pos pos = tileSiloInterface.siloConnectorPositionCache.get(page);
                    tileSiloInterface.fireSilo(pos, silos.get(siloIndex), player);
                }
            }
        }
        else if (button.id >= 10)
        {
            if (page >= 0 && page < tileSiloInterface.siloConnectorPositionCache.size())
            {
                int siloIndex = button.id - 10;
                List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(tileSiloInterface.siloConnectorPositionCache.get(page));
                if (siloIndex >= 0 && siloIndex < silos.size())
                {
                    Pos pos = tileSiloInterface.siloConnectorPositionCache.get(page);
                    tileSiloInterface.openSiloGui(pos, silos.get(siloIndex), player);
                }
            }
        }
    }

    protected final int maxPageCount()
    {
        return tileSiloInterface.siloConnectorPositionCache != null ? Math.max(tileSiloInterface.siloConnectorPositionCache.size() - 1, 0) : 0;
    }

    protected final int maxIndex()
    {
        if (page >= 0 && page <= maxPageCount())
        {
            List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(tileSiloInterface.siloConnectorPositionCache.get(page));
            return silos == null ? 0 : silos.size();
        }
        return 0;
    }
}
