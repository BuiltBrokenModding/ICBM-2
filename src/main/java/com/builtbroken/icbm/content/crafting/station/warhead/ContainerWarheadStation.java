package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class ContainerWarheadStation extends ContainerBase
{
    public ContainerWarheadStation(EntityPlayer player, TileWarheadStation inventory, int id)
    {
        super(player, inventory);

        if (id == 0)
        {
            //Intput Slot
            this.addSlotToContainer(new SlotWarhead(inventory, 0, 30, 10));

            //Explosive slot
            this.addSlotToContainer(new SlotExplosive(inventory, 1, 50, 37));

            //Trigger slot
            this.addSlotToContainer(new Slot(inventory, 3, 10, 37));

            //Output slot
            this.addSlotToContainer(new SlotWarhead(inventory, 2, 140, 25));

            //Player inventory
            this.addPlayerInventory(player);
        }
        else if (id == 1)
        {
            //Explosive slot
            this.addSlotToContainer(new SlotExplosive(inventory, 1, 10, 10));
        }
        else if (id == 2)
        {
            //Trigger slot
            this.addSlotToContainer(new Slot(inventory, 3, 10, 10));
        }
    }
}
