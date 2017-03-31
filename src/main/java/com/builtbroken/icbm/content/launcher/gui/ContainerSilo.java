package com.builtbroken.icbm.content.launcher.gui;

import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mc.prefab.inventory.BasicInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/23/2016.
 */
public class ContainerSilo extends ContainerBase
{
    EntityPlayer player;
    TileAbstractLauncher launcher;

    public BasicInventory basicInventory;

    public ContainerSilo(EntityPlayer player, TileAbstractLauncher launcher)
    {
        super(player, new BasicInventory(1));
        basicInventory = (BasicInventory) inventory;
        this.player = player;
        this.launcher = launcher;
        Slot slot = new Slot(inventory, 0, 112, 52);
        addSlotToContainer(slot);
        addPlayerInventory(player);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        //TODO Update client when item is encoded on the server
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if (!this.player.worldObj.isRemote)
        {
            for (int i = 0; i < this.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.inventory.getStackInSlotOnClosing(i);
                if (itemstack != null)
                {
                    if (!this.player.inventory.addItemStackToInventory(itemstack))
                    {
                        InventoryUtility.dropItemStack(new Location(player), itemstack);
                    }
                }
            }
            player.inventoryContainer.detectAndSendChanges();
        }
        launcher.returnGuiData.remove(player);
    }
}
