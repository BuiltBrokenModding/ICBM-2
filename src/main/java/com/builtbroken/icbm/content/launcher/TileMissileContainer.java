package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.data.missile.Missile;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.api.rails.IRailInventoryTile;
import com.builtbroken.mc.framework.block.imp.IActivationListener;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.tile.logic.TileMachineNode;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Prefab for anything that can store a missile
 * Created by robert on 1/18/2015.
 */
public class TileMissileContainer extends TileMachineNode<IInventory> implements IRailInventoryTile<IInventory>, IActivationListener
{
    /** Cached missile version of the stored missile item. */
    protected IMissile missile;

    public TileMissileContainer(String id, String mod)
    {
        super(id, mod);
    }

    @Override
    protected IInventory createInventory()
    {
        return new ExternalInventory(this, 1);
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        //Silo item
        ItemStack stack = ByteBufUtils.readItemStack(buf);
        if (stack.getItem() instanceof IMissileItem)
        {
            getInventory().setInventorySlotContents(0, stack);
        }
        else
        {
            getInventory().setInventorySlotContents(0, null);
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        //Silo item
        ByteBufUtils.writeItemStack(buf, getInventory().getStackInSlot(0) != null ? getInventory().getStackInSlot(0) : new ItemStack(Blocks.stone));
    }

    /**
     * Should the missile object be cached
     * when the item version changes?
     *
     * @return true if yes
     */
    protected boolean shouldCacheMissile()
    {
        return false;
    }

    @Override
    public boolean onPlayerActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        return playerRemoveMissile(player, side, hitX, hitY, hitZ) || playerAddMissile(player, side, hitX, hitY, hitZ);
    }

    public boolean playerRemoveMissile(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (player.getHeldItem() == null && getMissile() != null)
        {
            if (isServer())
            {
                //TODO add translation
                player.addChatComponentMessage(new ChatComponentText("*Removed Missile*"));
                player.inventory.mainInventory[player.inventory.currentItem] = getMissile().toStack();
                getInventory().setInventorySlotContents(0, null);
                player.inventoryContainer.detectAndSendChanges();
                sendDescPacket();
            }
            return true;
        }
        return false;
    }

    public boolean playerAddMissile(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
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
                    getInventory().setInventorySlotContents(0, stack);
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
     * Checks if the missile can be stored
     *
     * @param missile - missile
     * @return true if it can be stored
     */
    public boolean canAcceptMissile(IMissile missile)
    {
        return missile != null;
    }

    /**
     * Stores the missile into the tile
     * <p>
     * Coverts it into an item
     *
     * @param missile - missile, does not check if it is valid
     * @return true
     */
    public boolean storeMissile(IMissile missile)
    {
        if (getMissileItem() == null)
        {
            getInventory().setInventorySlotContents(0, missile.toStack());
            sendDescPacket();
            return true;
        }
        return false;
    }

    /**
     * Gets the missile object, converts from ItemStack
     * so cache if you plan to call often.
     *
     * @return new missile instance created from stored item
     */
    public IMissile getMissile()
    {
        if (shouldCacheMissile())
        {
            if (missile == null)
            {
                missile = createMissileObject();
            }
            if (missile != null)
            {
                return missile;
            }
        }
        return missile != null ? missile : createMissileObject();
    }

    protected IMissile createMissileObject()
    {
        return getMissileItem() != null && getMissileItem().getItem() instanceof IMissileItem ? ((IMissileItem) getMissileItem().getItem()).toMissile(getMissileItem()) : null;
    }

    protected boolean hasMissile()
    {
        return getMissileItem() != null && getMissileItem().getItem() instanceof IMissileItem;
    }

    /**
     * Gets the item for the missile
     *
     * @return ItemStack
     */
    public ItemStack getMissileItem()
    {
        return getInventory().getStackInSlot(0);
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (world() != null && slot == 0)
        {
            sendDescPacket();
            if (shouldCacheMissile())
            {
                missile = createMissileObject();
            }
        }
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
                missile = new Missile(slotStack);
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
