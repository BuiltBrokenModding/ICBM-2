package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/23/2016.
 */
public class GuiSiloInterface extends GuiContainerBase
{
    EntityPlayer player;
    TileSiloInterface tileSiloInterface;
    int page = 0;
    int index = 0;

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

        if (tileSiloInterface.clientSiloDataCache != null && tileSiloInterface.clientSiloDataCache.size() > 0 && tileSiloInterface.siloConnectorPositionCache.size() > 0)
        {
            if (page >= tileSiloInterface.siloConnectorPositionCache.size())
            {
                page = tileSiloInterface.siloConnectorPositionCache.size() - 1;
            }
            List<ISiloConnectionData> silos = tileSiloInterface.clientSiloDataCache.get(tileSiloInterface.siloConnectorPositionCache.get(page));
            if (silos != null && silos.size() > 0)
            {
                if (index >= silos.size())
                {
                    index = 0;
                }
                int j = 0;
                for (int i = index; i < silos.size() && i < index + 5; i++)
                {
                    String name = "Silo[" + i + "]"; //TODO add check if launcher or silo to update name
                    ISiloConnectionData data = silos.get(i);
                    if (data != null && data.getSiloName() != null && !data.getSiloName().isEmpty())
                    {
                        name = "Silo[" + data.getSiloName() + "]";
                    }
                    buttonList.add(new GuiButton(0, guiLeft + 6, guiTop + 10 + (j++ * 20), 100, 20, name));
                }
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
            int maxPage = tileSiloInterface.clientSiloDataCache == null ? 0 : tileSiloInterface.clientSiloDataCache.size() / 5;
            if (page < maxPage)
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
                page = tileSiloInterface.clientSiloDataCache == null ? 0 : tileSiloInterface.clientSiloDataCache.size() / 5;
            }
            initGui(); //Refresh
        }
    }
}
