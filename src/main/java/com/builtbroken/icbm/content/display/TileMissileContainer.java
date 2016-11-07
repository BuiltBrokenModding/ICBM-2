package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.rail.IRailInventoryTile;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Prefab for anything that can store a missile
 * Created by robert on 1/18/2015.
 */
public class TileMissileContainer extends TileModuleMachine implements IPacketIDReceiver, IRailInventoryTile
{
    public TileMissileContainer(String name, Material material)
    {
        this(name, material, 1);
    }

    public TileMissileContainer(String name, Material material, int slots)
    {
        super(name, material);
        this.addInventoryModule(slots);
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        //Silo item
        ItemStack stack = ByteBufUtils.readItemStack(buf);
        if (stack.getItem() instanceof IMissileItem)
        {
            this.setInventorySlotContents(0, stack);
        }
        else
        {
            this.setInventorySlotContents(0, null);
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        //Silo item
        ByteBufUtils.writeItemStack(buf, getStackInSlot(0) != null ? getStackInSlot(0) : new ItemStack(Blocks.stone));
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        return playerRemoveMissile(player, side, hit) || playerAddMissile(player, side, hit);
    }

    public boolean playerRemoveMissile(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() == null && getMissile() != null)
        {
            if (isServer())
            {
                //TODO add translation
                player.addChatComponentMessage(new ChatComponentText("*Removed Missile*"));
                player.inventory.mainInventory[player.inventory.currentItem] = getMissile().toStack();
                setInventorySlotContents(0, null);
                player.inventoryContainer.detectAndSendChanges();
                sendDescPacket();
            }
            return true;
        }
        return false;
    }

    public boolean playerAddMissile(EntityPlayer player, int side, Pos hit)
    {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && getMissile() == null)
        {
            if (isServer() && heldItem.getItem() instanceof IMissileItem)
            {
                IMissile missile = ((IMissileItem) heldItem.getItem()).toMissile(heldItem);

                if (canAcceptMissile(missile))
                {
                    //TODO add translation
                    player.addChatComponentMessage(new ChatComponentText("*Added Missile*"));
                    ItemStack stack = heldItem.copy();
                    stack.stackSize = 1;
                    setInventorySlotContents(0, stack);
                    if (!player.capabilities.isCreativeMode)
                    {
                        heldItem.stackSize--;
                        if (heldItem.stackSize <= 0)
                        {
                            player.inventory.mainInventory[player.inventory.currentItem] = null;
                        }
                        player.inventoryContainer.detectAndSendChanges();
                    }
                    sendDescPacket();
                }
                else
                {
                    //TODO add translation
                    player.addChatComponentMessage(new ChatComponentText("*Missile will not fit*"));
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Can the launcher accept the missile
     *
     * @param missile
     * @return
     */
    public boolean canAcceptMissile(IMissile missile)
    {
        return missile != null;
    }

    /**
     * Gets the missile object, converts from ItemStack
     * so cache if you plan to call often.
     *
     * @return new missile instance created from stored item
     */
    public IMissile getMissile()
    {
        return getMissileItem() != null && getMissileItem().getItem() instanceof IMissileItem ? ((IMissileItem) getMissileItem().getItem()).toMissile(getMissileItem()) : null;
    }

    /**
     * Gets the item for the missile
     *
     * @return ItemStack
     */
    public ItemStack getMissileItem()
    {
        return getStackInSlot(0);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public int[] getSlotsToLoad(ForgeDirection side)
    {
        return new int[]{0};
    }

    @Override
    public int[] getSlotsToUnload(ForgeDirection side)
    {
        return new int[]{0};
    }

    @Override
    public boolean canStore(ItemStack slotStack, int slot, ForgeDirection side)
    {
        if (slot == 0)
        {
            IMissile missile = null;
            if (slotStack.getItem() instanceof IMissileItem)
            {
                missile = ((IMissileItem) slotStack.getItem()).toMissile(slotStack);
            }
            else if (slotStack.getItem() instanceof IModuleItem)
            {
                IModule module = ((IModuleItem) slotStack.getItem()).getModule(slotStack);
                if (module instanceof IMissile)
                {
                    missile = (IMissile) module;
                }
            }
            else
            {
                missile = MissileModuleBuilder.INSTANCE.buildMissile(slotStack);
            }
            return missile != null && canAcceptMissile(missile);
        }
        return false;
    }

    @Override
    public boolean canRemove(ItemStack slotStack, int slot, ForgeDirection side)
    {
        return slot == 0;
    }

    @Override
    public boolean canStore(ItemStack stack, ForgeDirection side)
    {
        return true;
    }

    @Override
    public boolean canRemove(ItemStack stack, ForgeDirection side)
    {
        return true;
    }
}
