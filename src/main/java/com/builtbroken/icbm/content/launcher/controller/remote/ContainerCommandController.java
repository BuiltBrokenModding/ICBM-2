package com.builtbroken.icbm.content.launcher.controller.remote;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * Created by robert on 4/3/2015.
 */
public class ContainerCommandController extends ContainerBase
{
    public ContainerCommandController(EntityPlayer player, IInventory inventory)
    {
        super(player, inventory);
    }
}
