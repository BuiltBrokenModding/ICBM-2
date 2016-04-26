package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
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

/**
 * Created by robert on 1/18/2015.
 */
public class TileMissileContainer extends TileModuleMachine implements IPacketIDReceiver
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
            this.setInventorySlotContents(0, stack);
        else
            this.setInventorySlotContents(0, null);
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
        if(player.getHeldItem() == null && getMissile() != null)
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
        if(heldItem != null && getMissile() == null)
        {
            if (isServer() && heldItem.getItem() instanceof IMissileItem)
            {
                Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(heldItem);

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

    public boolean canAcceptMissile(Missile missile)
    {
        return missile != null;
    }

    public Missile getMissile()
    {
        return getMissileItem() != null ? MissileModuleBuilder.INSTANCE.buildMissile(getMissileItem()) : null;
    }

    public ItemStack getMissileItem()
    {
        return getStackInSlot(0);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }
}
