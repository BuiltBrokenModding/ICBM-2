package com.builtbroken.icbm.content.crafting.station.small.auto;

import com.builtbroken.icbm.client.gui.SlotEngine;
import com.builtbroken.icbm.client.gui.SlotGuidance;
import com.builtbroken.icbm.client.gui.SlotMissile;
import com.builtbroken.icbm.client.gui.SlotWarhead;
import com.builtbroken.icbm.content.missile.parts.casing.MissileCasings;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/13/2016.
 */
public class ContainerSMAutoCraft extends ContainerBase
{
    public ContainerSMAutoCraft(EntityPlayer player, TileSMAutoCraft inventory, int id)
    {
        super(player, inventory.getInventory());

        if (id == 0)
        {
            //Intput Slot
            this.addSlotToContainer(new SlotMissile(inventory.getInventory(), TileSMAutoCraft.INPUT_SLOT, 30, 10, MissileCasings.SMALL.ordinal()));

            //Engine slot
            this.addSlotToContainer(new SlotEngine(inventory.getInventory(), TileSMAutoCraft.ENGINE_SLOT, 50, 37));

            //Warhead slot
            this.addSlotToContainer(new SlotWarhead(inventory.getInventory(), TileSMAutoCraft.WARHEAD_SLOT, 10, 37));

            //Guidance slot
            this.addSlotToContainer(new SlotGuidance(inventory.getInventory(), TileSMAutoCraft.GUIDANCE_SLOT, 30, 37));

            //Output slot
            this.addSlotToContainer(new SlotMissile(inventory.getInventory(), TileSMAutoCraft.OUTPUT_SLOT, 140, 25, MissileCasings.SMALL.ordinal()));

            //Player inventory.getInventory()
            this.addPlayerInventory(player);
        }
        else if (id == 1)
        {
            //Warhead
            this.addSlotToContainer(new SlotWarhead(inventory.getInventory(), TileSMAutoCraft.WARHEAD_SLOT, 10, 10));
        }
        else if (id == 2)
        {
            //Guidance
            this.addSlotToContainer(new SlotGuidance(inventory.getInventory(), TileSMAutoCraft.GUIDANCE_SLOT, 10, 10));
        }
        else if (id == 3)
        {
            //Engine
            this.addSlotToContainer(new SlotEngine(inventory.getInventory(), TileSMAutoCraft.ENGINE_SLOT, 10, 10));
        }
    }
}
