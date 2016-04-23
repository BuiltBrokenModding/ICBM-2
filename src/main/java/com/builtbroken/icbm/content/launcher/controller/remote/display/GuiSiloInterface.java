package com.builtbroken.icbm.content.launcher.controller.remote.display;

import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/23/2016.
 */
public class GuiSiloInterface extends GuiContainerBase
{
    EntityPlayer player;
    TileSiloInterface tileSiloInterface;

    public GuiSiloInterface(EntityPlayer player, TileSiloInterface tileSiloInterface)
    {
        super(new ContainerDummy(player, tileSiloInterface));
        this.player = player;
        this.tileSiloInterface = tileSiloInterface;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        //TODO add button to pool information from the server
        //TODO add scroll bar or next button to go threw silo list
    }
}
