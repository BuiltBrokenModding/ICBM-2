package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class ContainerWarheadStation extends ContainerBase
{
    public ContainerWarheadStation(EntityPlayer player, TileWarheadStation inventory)
    {
        super(player, inventory);
        final int startX = 16;
        this.addSlotToContainer(new SlotWarhead(inventory, 0, startX + 20, 25));
        this.addSlotToContainer(new SlotExplosive(inventory, 1, startX + 60, 25));
        this.addSlotToContainer(new SlotWarhead(inventory, 2, startX + 100, 25));
        this.addPlayerInventory(player);
    }
}
