package com.builtbroken.icbm.content.launcher.controller.direct;

import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/27/2016.
 */
public class GuiSiloController extends GuiContainerBase
{
    TileSiloController controller;
    EntityPlayer player;

    public GuiSiloController(EntityPlayer player, TileSiloController controller)
    {
        super(new ContainerDummy());
        this.controller = controller;
        this.player = player;
    }
}
