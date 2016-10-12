package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.icbm.api.IWarheadItem;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Handles crafting and configuration of warheads
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/6/2016.
 */
public class TileWarheadStation extends TileModuleMachine implements IPacketIDReceiver
{
    public static final int WARHEAD_SLOT = 0;
    public static final int EXPLOSIVE_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    //TODO add tabs, crafting, configuration, reverse crafting
    //TODO add option to limit number of inserted explosives per craft (1-64 scale, all)

    public TileWarheadStation()
    {
        super("warheadStation", Material.iron);
        this.resistance = 10f;
        this.hardness = 10f;
    }

    /** Called to process a crafting request */
    public void doCrafting()
    {
        final ItemStack warheadStack = getWarheadStack();
        final ItemStack explosiveStack = getExplosiveStack();
        final ItemStack outputStack = getOutputStack();

        if (warheadStack != null && warheadStack.getItem() instanceof IWarheadItem && ExplosiveRegistry.get(explosiveStack) != null && (outputStack == null || outputStack.stackSize < outputStack.getMaxStackSize()))
        {
            final IWarhead warhead = ((IWarheadItem) warheadStack.getItem()).getModule(warheadStack);
            if (warhead != null && warhead.hasSpaceForExplosives(explosiveStack))
            {
                int insert = Math.min(explosiveStack.stackSize, warhead.getSpaceForExplosives());

                if (warhead.getExplosiveStack() == null)
                {
                    ItemStack insertStack = explosiveStack.copy();
                    insertStack.stackSize = insert;
                    warhead.setExplosiveStack(insertStack);
                }
                else
                {
                    //Increase explosive stack
                    warhead.getExplosiveStack().stackSize += insert;
                    //Trigger any events for warhead change
                    warhead.setExplosiveStack(warhead.getExplosiveStack());
                }
                final ItemStack newWarhead = warhead.toStack();

                if (outputStack == null || InventoryUtility.stacksMatch(newWarhead, outputStack))
                {
                    //Decrease explosive stack
                    explosiveStack.stackSize -= insert;
                    if (explosiveStack.stackSize <= 0)
                    {
                        getInventory().setInventorySlotContents(EXPLOSIVE_SLOT, null);
                    }
                    else
                    {
                        //Update inventory
                        getInventory().setInventorySlotContents(EXPLOSIVE_SLOT, explosiveStack);
                    }

                    //Decrease warhead stack (INPUT)
                    warheadStack.stackSize--;
                    if (warheadStack.stackSize <= 0)
                    {
                        getInventory().setInventorySlotContents(WARHEAD_SLOT, null);
                    }
                    else
                    {
                        //Update inventory
                        getInventory().setInventorySlotContents(WARHEAD_SLOT, warheadStack);
                    }

                    //Increase output
                    if (getOutputStack() == null)
                    {
                        getInventory().setInventorySlotContents(OUTPUT_SLOT, newWarhead);
                    }
                    else
                    {
                        outputStack.stackSize += 1;
                        getInventory().setInventorySlotContents(OUTPUT_SLOT, outputStack);
                    }
                }
            }
        }
    }


    @Override
    public IInventory getInventory()
    {
        if (super.getInventory() == null)
        {
            //lazy init of inventory
            addInventoryModule(3);
        }
        return super.getInventory();
    }

    protected ItemStack getWarheadStack()
    {
        return getInventory().getStackInSlot(WARHEAD_SLOT);
    }

    protected ItemStack getExplosiveStack()
    {
        return getInventory().getStackInSlot(EXPLOSIVE_SLOT);
    }

    protected ItemStack getOutputStack()
    {
        return getInventory().getStackInSlot(OUTPUT_SLOT);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        super.doUpdateGuiUsers();
        if(!super.read(buf, id, player, type))
        {
            if(isServer())
            {
                if(id == 1)
                {
                    doCrafting();
                    return true;
                }
            }
            else
            {

            }
            return false;
        }
        return true;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        //TODO read to inventory local
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        //TODO send inventory
    }

    @Override
    public void doUpdateGuiUsers()
    {
        if(ticks % 5 == 0)
        {
            //PacketTile packet = new PacketTile(this, 5, );
        }
    }

    public void sendCraftingPacket()
    {
        sendPacketToServer(new PacketTile(this, 1));
    }
}
