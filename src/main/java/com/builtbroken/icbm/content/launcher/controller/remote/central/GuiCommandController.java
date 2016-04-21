package com.builtbroken.icbm.content.launcher.controller.remote.central;

import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/21/2016.
 */
public class GuiCommandController extends GuiContainerBase
{
    public GuiCommandController(EntityPlayer player, TileCommandController controller)
    {
        super(new ContainerCommandController(player, controller));
    }
}
