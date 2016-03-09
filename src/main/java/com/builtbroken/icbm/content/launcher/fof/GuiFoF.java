package com.builtbroken.icbm.content.launcher.fof;

import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2016.
 */
public class GuiFoF extends GuiContainerBase
{
    protected TileFoF fof;

    public GuiFoF(TileFoF fof, EntityPlayer player)
    {
        super(new ContainerFoF(player, fof));
    }
}
