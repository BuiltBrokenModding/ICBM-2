package com.builtbroken.icbm.content.crafting.station;

import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Created by robert on 3/12/2015.
 */
public class GuiMissileWorkstation extends GuiContainerBase
{
    private TileMissileWorkstation tile;

    public GuiMissileWorkstation(EntityPlayer player, TileMissileWorkstation tile)
    {
        super(new ContainerMissileWorkstation(player, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        //Draw slots
        this.drawSlot(inventorySlots.getSlot(0));
        this.drawSlot(inventorySlots.getSlot(1));
        this.drawSlot(inventorySlots.getSlot(2));
        this.drawSlot(inventorySlots.getSlot(3));
        this.drawSlot(inventorySlots.getSlot(0));
        this.drawSlot(inventorySlots.getSlot(4));
    }
}
