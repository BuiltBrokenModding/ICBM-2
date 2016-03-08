package com.builtbroken.icbm.content.ams;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2016.
 */
public class ContainerAMSTurret extends ContainerBase
{
    public ContainerAMSTurret(EntityPlayer player, TileAMS ams)
    {
        super(player, ams);
        for (int slotX = 0; slotX < 5; slotX++)
        {
            addSlotToContainer(new Slot(ams, slotX + 5, 44 + slotX * 18, 40));
            addSlotToContainer(new Slot(ams, slotX, 44 + slotX * 18, 22));
        }
        addPlayerInventory(player);
    }
}
