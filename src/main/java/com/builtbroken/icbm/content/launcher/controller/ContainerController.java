package com.builtbroken.icbm.content.launcher.controller;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * Created by robert on 4/3/2015.
 */
public class ContainerController extends ContainerBase
{
    public ContainerController(EntityPlayer player, IInventory inventory)
    {
        super(player, inventory);
    }
}
