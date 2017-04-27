package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.api.rails.IRailInventoryTile;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
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
public class TileMissileContainer extends TileModuleMachine<IInventory> implements IPacketIDReceiver, IRailInventoryTile<IInventory>
{
    /** Cached missile version of the stored missile item. */
    protected IMissile missile;

    public TileMissileContainer(String name, Material material)
    {
        super(name, material);
    }

    @Override
    protected IInventory createInventory()
    {
        return new TileModuleInventory(this, 1);
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
            setInventorySlotContents(0, missile.toStack());
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
        return createMissileObject();
    }

    protected IMissile createMissileObject()
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
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (slot == 0)
        {
            sendDescPacket();
            if (shouldCacheMissile())
            {
                missile = createMissileObject();
            }
        }
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