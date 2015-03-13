package com.builtbroken.icbm.content.crafting.station;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created by robert on 3/12/2015.
 */
public class ContainerMissileWorkstation extends ContainerBase
{
    public ContainerMissileWorkstation(EntityPlayer player, IInventory inventory)
    {
        super(player, inventory);
        this.addSlotToContainer(new Slot(inventory, 0, 50, 50));
        this.addSlotToContainer(new Slot(inventory, 1, 70, 50));
        this.addSlotToContainer(new Slot(inventory, 2, 90, 50));
        this.addSlotToContainer(new Slot(inventory, 3, 110, 50));
        this.addSlotToContainer(new Slot(inventory, 4, 130, 50));
        addPlayerInventory(player);
    }
}
