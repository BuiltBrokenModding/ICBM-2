package com.builtbroken.icbm.content.ams;

import com.builtbroken.mc.core.References;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2016.
 */
public class GuiAMSTurret extends GuiContainerBase
{
    protected final TileAMS ams;

    public GuiAMSTurret(EntityPlayer player, TileAMS ams)
    {
        super(new ContainerAMSTurret(player, ams));
        this.ams = ams;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        for (Object object : inventorySlots.inventorySlots)
        {
            Slot slot = (Slot) object;
            if(slot.inventory == ams && slot.getHasStack() && !ams.isAmmo(slot.getStack()))
            {
                this.mc.renderEngine.bindTexture(References.GUI_COMPONENTS);
                this.drawTexturedModalRect(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1, 0, 18 * 12, 18, 18);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        for (Object object : inventorySlots.inventorySlots)
        {
            drawSlot((Slot) object);
        }
    }
}
